package ca.shapedetector;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import std.Picture;
import ca.CA;
import ca.CACell;
import ca.Stopwatch;
import ca.rules.cacell.CACellRule;
import ca.rules.cacell.CAEdgeFinderRule;
import ca.rules.cacell.CANoiseRemoverRule;
import ca.rules.cacell.CAOutlineFinderRule;
import ca.rules.cacell.CAProtoShapeAssociationRule;
import ca.rules.cacell.CAShapeFinderRule;
import ca.rules.protoshape.CAProtoShapeIdentifierRule;
import ca.shapedetector.shapes.SDRectangle;
import ca.shapedetector.shapes.SDShape;

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
	/** Set of unique protoShapes. */
	protected Set<CAProtoShape> protoShapes;
	/** List of detected shapes. */
	protected List<SDShape> shapes;

	private class ProtoShapeSorter implements Comparable<ProtoShapeSorter> {
		CAProtoShape protoShape;

		public ProtoShapeSorter(CAProtoShape protoShape) {
			this.protoShape = protoShape;
		}

		@Override
		public int compareTo(ProtoShapeSorter arg0) {
			double x1 = (protoShape.getBoundaries()[0][1] - protoShape
					.getBoundaries()[0][0]) / 2.0;
			double y1 = (protoShape.getBoundaries()[1][1] - protoShape
					.getBoundaries()[1][0]) / 2.0;

			double x2 = (arg0.protoShape.getBoundaries()[0][1] - arg0.protoShape
					.getBoundaries()[0][0]) / 2.0;
			double y2 = (arg0.protoShape.getBoundaries()[1][1] - arg0.protoShape
					.getBoundaries()[1][0]) / 2.0;

			if (x1 == x2 && y1 == y2) {
				return 0;
			} else if (y1 < y2) {
				return -1;
			} else {
				if (x1 < x2) {
					return -1;
				} else {
					return 1;
				}
			}
		}

	}

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

		// picture = Filter.greyscale(picture);
		// picture = Filter.monochrome(picture);
		// picture = Posterize.apply(picture, 3);

		Stopwatch stopwatch = new Stopwatch();
		CAShapeDetector shapeDetector = new CAShapeDetector(epsilon, r);
		picture = shapeDetector.apply(picture);

		System.out.println("Finished in " + stopwatch.time() + " ms");
		shapeDetector.draw();
	}

	protected void createGUI() {
		super.createGUI();
		frame.setTitle("CA Shape Detector");
	}

	protected Set<CAProtoShape> sortProtoShapes(Set<CAProtoShape> shapes) {
		ProtoShapeSorter[] shapeSorter = new ProtoShapeSorter[shapes.size()];
		Iterator<CAProtoShape> iterator = shapes.iterator();
		for (int i = 0; i < shapes.size(); i++) {
			shapeSorter[i] = new ProtoShapeSorter(iterator.next());
		}
		Arrays.sort(shapeSorter);
		Set<CAProtoShape> sortedShapes = new LinkedHashSet<CAProtoShape>();
		for (ProtoShapeSorter s : shapeSorter) {
			sortedShapes.add(s.protoShape);
		}
		return sortedShapes;
	}

	public CAShapeDetector(float epsilon, int r) {
		super(epsilon, r);
		// neighbourhoodModel = VANNEUMANN_NEIGHBOURHOOD;

		drawOnModelUpdate = true;
		// drawOnCellUpdate = true;

		cellRules = new LinkedList<CACellRule>();
//		cellRules.add(new CADummyRule(this));
		// cellRules.add(new CAGatherNeighboursRule(this));
		cellRules.add(new CANoiseRemoverRule(this));
		cellRules.add(new CAEdgeFinderRule(this));
		cellRules.add(new CAProtoShapeAssociationRule(this));
		cellRules.add(new CAShapeFinderRule(this));
		// cellRules.add(new CAShapeAssimilatorRule(this));
		cellRules.add(new CAOutlineFinderRule(this));
	}

	@Override
	public void setPicture(Picture picture) {
		super.setPicture(picture);
		shapeAssociations = new CAProtoShape[picture.width()][picture.height()];
		protoShapes = Collections.synchronizedSet(new HashSet<CAProtoShape>(
				lattice.length * lattice[0].length));
	}

	@Override
	public Picture apply(Picture picture) {
		super.apply(picture);
		shapes = new LinkedList<SDShape>();

		/*
		 * To ease debugging, sort the shapes in some kind of order instead of
		 * at random.
		 */
		protoShapes = sortProtoShapes(protoShapes);

		CAProtoShapeIdentifierRule protoShapeIdentifier = new CAProtoShapeIdentifierRule(
				this);

		for (CAProtoShape protoShape : protoShapes) {
			protoShapeIdentifier.update(protoShape);
		}
		protoShapeIdentifier.printTimers();

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
		return mergeProtoShapes(getProtoShape(cell1), getProtoShape(cell2));
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
	protected CAProtoShape mergeProtoShapes(CAProtoShape shape1,
			CAProtoShape shape2) {
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
					setProtoShape(cell, newShape);
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
	public CAProtoShape getProtoShape(CACell cell) {
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
	public void setProtoShape(CACell cell, CAProtoShape shape) {
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
		for (SDShape shape : shapes) {
			/* using instanceof does not seem to work here. */
			// if (shape.getClass() != SDUnknownShape.class) {
			/* Ignore the rectangle detected at the image borders. */
			if (shape instanceof SDRectangle
					&& shape.getDimensions()[0] + delta > pictureBefore.width()
					&& shape.getDimensions()[1] + delta > pictureBefore
							.height()) {
				continue;
			}
			// System.out.println(shape);
			// shape.draw();
			// }
		}
		return picture;
	}

	/**
	 * Gets the list of shapes found.
	 * 
	 * @return List of shapes found.
	 */
	public List<SDShape> getShapes() {
		return shapes;
	}

	public void addShape(SDShape shape) {
		shapes.add(shape);
	}

	public void addProtoShape(CAProtoShape protoShape) {
		protoShapes.add(protoShape);
	}
}
