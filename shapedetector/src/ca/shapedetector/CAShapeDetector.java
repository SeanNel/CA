package ca.shapedetector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import std.Picture;
import std.StdDraw;

import ca.CA;
import ca.CACell;
import ca.Stopwatch;
import ca.rules.cacell.*;
import ca.rules.protoshape.*;
import ca.shapedetector.shapes.*;

/**
 * Finds shapes in an image. Accepts bmp, png and jpg images.
 * <p>
 * Usage: CAShapeDetector <image_path>
 * <p>
 * Some images to test with are:
 * <p>
 * img.png: Greyscale image with dithered patterns.
 * <p>
 * test1.png: Simple monochrome image.
 * <p>
 * pipe.png: Low contrast image with text.
 * <p>
 * batman.jpg: Low monochrome contrast image.
 * <p>
 * shapes.png: Low monochrome contrast image.
 * 
 * @author Sean
 */
public class CAShapeDetector extends CA {
	/** Table mapping cells to protoShapes. */
	protected CAProtoShape[][] shapeAssociations;
	/** List of unique protoShapes. */
	protected Set<CAProtoShape> protoShapes;
	/** List of detected shapes. */
	protected List<CAShape> shapes;

	/**
	 * Applies shape detector to image given as argument on the command line.
	 * 
	 * @param path
	 *            Path to image. Accepts bmp, png and jpg images.
	 * @param epsilon
	 *            The difference threshold expressed as a fraction.
	 * @param r
	 *            Search radius. Determines the size of the neighbourhood.
	 */
	public static void main(String[] args) {
		StdDraw.frame.setTitle("CA Shape Detector");

		String path;
		float epsilon = 0.05f;
		int r = 1;

		if (args.length == 0) {
			System.out
					.println("Please specify a path to the image to process.");
			return;
		} else {
			path = args[0];
		}
		if (args.length > 1) {
			epsilon = Float.parseFloat(args[1]);
		}
		if (args.length > 2) {
			r = Integer.parseInt(args[2]);
		}

		Picture picture = new Picture(path);
		StdDraw.setCanvasSize(picture.width(), picture.height());
		picture.setOriginUpperLeft();

		// picture = Filter.greyscale(picture);
		// picture = Filter.monochrome(picture);
		// picture = Posterize.apply(picture, 3);

		Stopwatch stopwatch = new Stopwatch();
		CAShapeDetector shapeDetector = new CAShapeDetector(epsilon, r);
		picture = shapeDetector.apply(picture);

		System.out.println("Finished in " + stopwatch.time() + " ms");
		StdDraw.picture(0.5, 0.5, picture.getImage());
	}

	public CAShapeDetector(float epsilon, int r) {
		super(epsilon, r);
		// neighbourhoodModel = VANNEUMANN_NEIGHBOURHOOD;

		processes = new ArrayList<CACellRule>(3);
		processes.add(new CANoiseRemoverRule(this));
		processes.add(new CAEdgeFinderRule(this));
		processes.add(new CAShapeFinderRule(this));
		processes.add(new CAOutlineFinderRule(this));
	}

	@Override
	public void setPicture(Picture picture) {
		stopwatch.start();

		super.setPicture(picture);
		shapeAssociations = new CAProtoShape[picture.width()][picture.height()];
		/*
		 * HashSet performs better with the remove method than LinkedList, which
		 * performs better than ArrayList. Might have to make this
		 * Collections.synchronizedSet(...);
		 */
		protoShapes = new HashSet<CAProtoShape>(lattice.length
				* lattice[0].length);
		/*
		 * Would it be better to do this later in parallel? Doesn't seem to take
		 * much time anyway.
		 */
		for (int x = 0; x < lattice.length; x++) {
			for (int y = 0; y < lattice[0].length; y++) {
				CAProtoShape shape = new CAProtoShape(getCell(x, y));
				shapeAssociations[x][y] = shape;
				protoShapes.add(shape);
			}
		}

		stopwatch.print(this.getClass().getSimpleName() + " loading time: ");
	}

	@Override
	public Picture apply(Picture picture) {
		/* Skips integrated noise removal & edge finding. */
		// passes = 2;

		super.apply(picture);
		shapes = new LinkedList<CAShape>();
		Set<CAProtoShape> oldProtoShapes = new HashSet<CAProtoShape>(
				protoShapes);
		CAProtoShapeAssimilatorRule shapeAssimilator = new CAProtoShapeAssimilatorRule(
				this);
		/*
		 * TODO: order shapes in terms of area, then take a subset to
		 * assimilate. Should guarantee (O)NlogN performance instead of (O)N as
		 * done here.
		 */
		Stopwatch assimilatorStopwatch = new Stopwatch();
		for (CAProtoShape shape : oldProtoShapes) {
			shapeAssimilator.update(shape);
		}
		assimilatorStopwatch.print("Assimilated shapes: ");

		CAProtoShapeIdentifierRule protoShapeIdentifier = new CAProtoShapeIdentifierRule(
				this);

		for (CAProtoShape protoShape : protoShapes) {
			protoShapeIdentifier.update(protoShape);
		}

		return pointOutShapes(pictureAfter);
	}

	/**
	 * Merges two cells' shapes together.
	 * 
	 * @param cell1
	 *            1st cell to merge with.
	 * @param cell2
	 *            2nd cell to merge with.
	 * @return The resulting protoShape.
	 */
	public synchronized CAProtoShape mergeCells(CACell cell1, CACell cell2) {
		if (cell1 == null || cell2 == null) {
			return null;
		} else {
			return mergeShapes(getShape(cell1), getShape(cell2));
		}
	}

	/**
	 * Merges 2 protoShapes together.
	 * 
	 * @see CAProtoShapeMergerThread
	 * @param shape1
	 *            1st protoShape to merge with.
	 * @param shape2
	 *            2st protoShape to merge with.
	 * @return The resulting protoShape.
	 */
	protected CAProtoShape mergeShapes(CAProtoShape shape1, CAProtoShape shape2) {
		/* NB */
		if (shape1 == shape2) {
			return null;
		}
		synchronized (shape1) {
			synchronized (shape2) {
				/*
				 * Improves efficiency by merging the smaller shape into the
				 * larger shape.
				 */
				CAProtoShape newShape;
				CAProtoShape oldShape;
				if (shape1.getArea() > shape2.getArea()) {
					newShape = shape1;
					oldShape = shape2;
				} else {
					newShape = shape2;
					oldShape = shape1;
				}

				for (CACell cell : oldShape.getAreaCells()) {
					setShape(cell, newShape);
				}
				/* Must be removed before merging. */
				protoShapes.remove(oldShape);
				newShape.merge(oldShape);
				return newShape;
			}
		}
	}

	/**
	 * Gets the shape associated with the specified cell.
	 * 
	 * @param cell
	 *            A cell belonging to a shape.
	 * @return The shape associated with the specified cell.
	 */
	public CAProtoShape getShape(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		return shapeAssociations[coordinates[0]][coordinates[1]];
	}

	/**
	 * Sets the shape associated with the specified cell.
	 * 
	 * @param cell
	 *            A cell belonging to a shape.
	 * @param shape
	 *            The shape associated with the specified cell.
	 */
	public void setShape(CACell cell, CAProtoShape shape) {
		int[] coordinates = cell.getCoordinates();
		shapeAssociations[coordinates[0]][coordinates[1]] = shape;
	}

	/**
	 * Displays which shapes were found where.
	 * 
	 * @param picture
	 *            Picture to render to.
	 * @return Rendered picture.
	 */
	public Picture pointOutShapes(Picture picture) {
		System.out.println("Number of shapes: " + shapes.size());
		System.out.println("Detected shapes: ");
		int delta = 2;
		for (CAShape shape : shapes) {
			/* using instanceof does not seem to work here. */
			if (shape.getClass() != CAUnknownShape.class) {
				/* Ignore the rectangle detected at the image borders. */
				if (shape instanceof CARectangle
						&& shape.getDimensions()[0] + delta > pictureBefore
								.width()
						&& shape.getDimensions()[1] + delta > pictureBefore
								.height()) {
					continue;
				}
				System.out.println(shape);
				shape.draw();
			}
		}
		return picture;
	}

	/**
	 * Gets the list of shapes found.
	 * 
	 * @return List of shapes found.
	 */
	public List<CAShape> getShapes() {
		return shapes;
	}

	public void addShape(CAShape shape) {
		shapes.add(shape);
	}
}
