package ca.shapedetector;

import graphics.ColourCompare;

import java.awt.Color;
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
import ca.shapedetector.shapes.CARectangle;
import ca.shapedetector.shapes.CAShape;
import ca.shapedetector.shapes.CAUnknownShape;

/**
 * Finds shapes in an image.
 * <p>
 * Usage: CAShapeDetector <image_path>
 * <p>
 * On the (optional) 1st pass, finds the edges. It seems better to do this from
 * a separate CA with r > 1. It is unclear whether doing it here would bring
 * much performance benefit.
 * <p>
 * On the 2nd pass, finds the outlines, ensuring that outlines are closed loops,
 * and in so doing also groups cells together into shapes.
 * <p>
 * On the 2nd pass, the outlines of shapes are found.
 * <p>
 * On the 3rd pass, insignificant shapes are assimilated into neighbouring
 * shapes. (seems to work)
 * <p>
 * The CA is done. Now outline cells are to calculate their gradients.
 * <p>
 * Then shapes are requested to identify themselves (as circles, squares and so
 * on).
 * <p>
 * Note that the apparent thickness of edges is irrelevant. Each shape's outline
 * is determined by a single layer of cells and this is ensured by the
 * algorithm.
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
	 * Shapes with areas smaller than this will be assimilated into larger
	 * shapes.
	 */
	protected int minArea = 25;
	/**
	 * Colour that cells turn to when they become inactive, that is the
	 * background colour of the output image.
	 */
	public final static Color QUIESCENT_COLOUR = new Color(255, 255, 255);
	/**
	 * Colour that edge cells turn to, that is the foreground colour of the
	 * output image.
	 */
	public final static Color EDGE_COLOUR = new Color(200, 200, 200);

	/**
	 * Applies shape detector to image given as argument on the command line.
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
	 * @param args
	 *            Path to image. Accepts bmp, png and jpg images.
	 */
	public static void main(String[] args) {
		StdDraw.frame.setTitle("CA Shape Detector");
		Stopwatch stopwatch = new Stopwatch();

		if (args.length == 0) {
			System.out
					.println("Please specify a path to the image to process.");
			return;
		}

		Picture picture = new Picture(args[0]);
		StdDraw.setCanvasSize(picture.width(), picture.height());
		picture.setOriginUpperLeft();

		// picture = Filter.greyscale(picture);
		// picture = Filter.monochrome(picture);
		// picture = Posterize.apply(picture, 3);

		CAShapeDetector shapeDetector = new CAShapeDetector(0.05f, 1); //0.05f, 1
		picture = shapeDetector.apply(picture);

		System.out.println("Finished in " + stopwatch.time() + " ms");
		StdDraw.picture(0.5, 0.5, picture.getImage());
	}

	public CAShapeDetector(float epsilon, int r) {
		super(epsilon, r);
		// neighbourhoodModel = VANNEUMANN_NEIGHBOURHOOD;
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
		protoShapes = new HashSet<CAProtoShape>();
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
//		passes = 2;

		Picture output = super.apply(picture);
		output = pointOutShapes(output);
		return output;
	}

	/* For performance profiling */
	public long time;

	@Override
	protected void endPass() {
		super.endPass();

		// System.out.println("TIME: " + time);
		// time = 0;
	}

	// Stopwatch stopwatch = new Stopwatch();
	// time += stopwatch.time();

	@Override
	protected void postProcess() {
		Stopwatch stopwatch = new Stopwatch();
		long t1 = 0;
		long t2 = 0;
		long t3 = 0;
		long t4 = 0;
		Set<CAProtoShape> oldProtoShapes = new HashSet<CAProtoShape>(
				protoShapes);
		/*
		 * TODO: order shapes in terms of area, then take a subset to
		 * assimilate. Should guarantee (O)NlogN performance instead of (O)N as
		 * done here.
		 */
		for (CAProtoShape shape : oldProtoShapes) {
			/* Assimilates insignificant shapes into neighbouring ones. */
			if (shape.getArea() < minArea) {
				/*
				 * May be null if this shape has already been merged from this
				 * loop...
				 */
				if (shape.getOutlineCells() != null) {
					assimilateShape(shape);
				} else {
					continue;
				}
			}
		}
		t1 = stopwatch.time();

		CAShape shapeDetector = new CAShape(pictureAfter);

		shapes = new LinkedList<CAShape>();
		for (CAProtoShape protoShape : protoShapes) {
			// System.out.println("*** " + protoShape);
			/* Arranges outline cells in order. */
			stopwatch.start();
			// protoShape.orderOutlineCells();
			t2 += stopwatch.time();

			/* Calculates gradients. */
			stopwatch.start();
			protoShape.calculateGradients();
			t3 += stopwatch.time();

			/* Identifies shapes. */
			stopwatch.start();
			shapes.add(shapeDetector.identifyShape(protoShape));
			t4 += stopwatch.time();
		}

		System.out.println("Assimilated shapes: " + t1 + " ms");
		System.out.println("Arranged outlines: " + t2 + " ms");
		System.out.println("Calculated gradients: " + t3 + " ms");
		System.out.println("Identified shapes: " + t4 + " ms");
	}

	/**
	 * Merges two cells' shapes together.
	 * 
	 * @param cell1
	 *            1st cell to merge with.
	 * @param cell2
	 *            2nd cell to merge with.
	 */
	public synchronized void mergeCells(CACell cell1, CACell cell2) {
		mergeShapes(getShape(cell1), getShape(cell2));
	}

	/**
	 * Merges 2 shapes together.
	 * 
	 * @see CAProtoShapeMergerThread
	 * @param shape1
	 *            1st shape to merge with.
	 * @param shape2
	 *            2st shape to merge with.
	 */
	protected void mergeShapes(CAProtoShape shape1, CAProtoShape shape2) {
		if (shape1 == shape2) {
			return; /* NB */
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

	@Override
	public void updateCell(CACell cell) {
		switch (passes) {
		case 0:
			initCell(cell);
			removeNoise(cell);
			active = true;
			break;
		case 1:
			/* Resetting the neighbourhood may improve performance for r > 1 */
			// cell.setNeighbourhood(gatherCloseNeighbours(cell));
			findEdges(cell);
			active = true;
			break;
		case 2:
			// cell.getNeighbourhood().clear(); /* Free the memory? */
			cell.setNeighbourhood(gatherCardinalNeighbours(cell));
			findShapes(cell);
			active = true;
			break;
		case 3:
			findOutlines(cell);
			break;
		}
		// System.out.println(cell);
	}

	/**
	 * Caches the neighbouring cells of the specified cell. Optimized for Moore
	 * neighbourhood, r=1. Does not include the cell in its own neighbourhood.
	 * 
	 * @param cell
	 *            The cell to initialize.
	 */
	protected List<CACell> gatherCloseNeighbours(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		List<CACell> neighbourhood = new ArrayList<CACell>();
		// neighbourhood.add(getCell(coordinates[0], coordinates[1]));
		neighbourhood.add(getCell(coordinates[0], coordinates[1] - 1));
		neighbourhood.add(getCell(coordinates[0] + 1, coordinates[1] - 1));
		neighbourhood.add(getCell(coordinates[0] + 1, coordinates[1]));
		neighbourhood.add(getCell(coordinates[0] + 1, coordinates[1] + 1));
		neighbourhood.add(getCell(coordinates[0], coordinates[1] + 1));
		neighbourhood.add(getCell(coordinates[0] - 1, coordinates[1] + 1));
		neighbourhood.add(getCell(coordinates[0] - 1, coordinates[1]));
		neighbourhood.add(getCell(coordinates[0] - 1, coordinates[1] - 1));
		return neighbourhood;
	}

	/**
	 * Caches the neighbouring cells of the specified cell. Optimized for
	 * VanNeumann neighbourhood, r=1. Does not include the cell in its own
	 * neighbourhood.
	 * 
	 * @param cell
	 *            The cell to initialize.
	 */
	protected List<CACell> gatherCardinalNeighbours(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		List<CACell> neighbourhood = new ArrayList<CACell>();
		// neighbourhood.add(getCell(coordinates[0], coordinates[1]));
		neighbourhood.add(getCell(coordinates[0], coordinates[1] - 1));
		neighbourhood.add(getCell(coordinates[0], coordinates[1] + 1));
		neighbourhood.add(getCell(coordinates[0] - 1, coordinates[1]));
		neighbourhood.add(getCell(coordinates[0] + 1, coordinates[1]));
		return neighbourhood;
	}

	/**
	 * Gathers the specified cell's Moore neighbourhood with r=1, not including
	 * the current cell. Places cells in clockwise order, starting with the cell
	 * directly above this one.
	 * 
	 * @param cell
	 *            Cell to get neighbourhood of.
	 * @return The cell's neighbourhood.
	 */
	protected List<CACell> meetOutlineNeighbours(CACell cell) {
		return gatherCloseNeighbours(cell);
	}

	/**
	 * Removes noise from image.
	 * 
	 * @param cell
	 */
	public void removeNoise(CACell cell) {
		float maxDifference = 0f;

		List<CACell> neighbourhood = cell.getNeighbourhood();
		Color[] colours = new Color[neighbourhood.size()];
		int i = 0;
		for (CACell neighbour : neighbourhood) {
			if (neighbour == cell || neighbour == paddingCell) {
				continue;
			}
			Color colour = getColour(neighbour);
			colours[i++] = colour;
			float difference = ColourCompare.getDifference(getColour(cell),
					colour);
			if (difference > maxDifference) {
				maxDifference = difference;
			}
		}

		Color averageColour = ColourCompare.averageColour(colours);

		if (maxDifference < epsilon) {
			/*
			 * Sets pixel to the average colour of the surrounding pixels. Has a
			 * blurring effect.
			 */
			setColour(cell, averageColour);
		}
	}

	/**
	 * Finds the edges in the image.
	 * 
	 * @param cell
	 */
	public void findEdges(CACell cell) {
		List<CACell> neighbourhood = cell.getNeighbourhood();
		for (CACell neighbour : neighbourhood) {
			if (neighbour != cell && neighbour != paddingCell) {
				float difference = ColourCompare.getDifference(getColour(cell),
						getColour(neighbour));
				if (difference > epsilon) {
					setColour(cell, EDGE_COLOUR);
					return;
				}
			}
		}
		setColour(cell, QUIESCENT_COLOUR);
	}

	/**
	 * Groups cells of similar colour together into shapes.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void findShapes(CACell cell) {
		List<CACell> neighbourhood = cell.getNeighbourhood();
		for (CACell neighbour : neighbourhood) {
			if (neighbour != cell && neighbour != paddingCell
					&& getShape(neighbour) != getShape(cell)) {
				float difference = ColourCompare.getDifference(getColour(cell),
						getColour(neighbour));
				if (difference < epsilon) {
					mergeCells(cell, neighbour);
				}
			}
		}
	}

	/**
	 * Assimilates the specified shape into a neighbouring shape if it is too
	 * small to be of importance.
	 * 
	 * @param shape
	 *            Shape to assimilate.
	 */
	public void assimilateShape(CAProtoShape shape) {
		/** A list of all the shapes next to this one. */
		Set<CAProtoShape> neighbouringShapes = new HashSet<CAProtoShape>();
		/*
		 * Gathers all the shapes next to this one. Duplicates would slow down
		 * the next step.
		 */
		for (CACell cell : shape.getOutlineCells()) {
			List<CACell> neighbourhood = cell.getNeighbourhood();
			for (CACell neighbour : neighbourhood) {
				if (neighbour != cell && neighbour != paddingCell) {
					CAProtoShape neighbouringShape = getShape(neighbour);
					if (neighbouringShape != shape) {
						neighbouringShapes.add(neighbouringShape);
					}
				}
			}
		}

		/** This gives the least difference to a neighbouring shape. */
		float minDifference = 2f;
		/**
		 * A representative shape from the neighbouring shape most similar to
		 * this shape.
		 * <p>
		 * Referencing a representative cell of the shape instead of the shape
		 * itself helps avoid synchronization issues.
		 */
		CACell similarCell = null;

		/*
		 * Finds a representative cell from the shape next to this one that is
		 * most similar to this shape.
		 */
		for (CAProtoShape neighbouringShape : neighbouringShapes) {
			Color colour1 = getShapeAverageColour(shape);
			Color colour2;
			// synchronized (neighbouringShape) {
			CACell repCell = neighbouringShape.getAreaCells().get(0);
			/*
			 * getShapeAverageColour is expensive, so instead we can compare the
			 * representative cell with this one.
			 */
			colour2 = getColour(repCell); // getShapeAverageColour(neighbouringShape);
			// }
			float difference = ColourCompare.getDifference(colour1, colour2);
			if (difference < minDifference) {
				minDifference = difference;
				similarCell = repCell; // neighbouringShape.getAreaCells().get(0)
			}
		}

		/*
		 * Merging representative cells of the two shapes instead of the shapes
		 * themselves helps avoid synchronization issues.
		 */
		mergeCells(shape.getAreaCells().get(0), similarCell);
	}

	/**
	 * Gets the average colour of the shape.
	 * <p>
	 * This is calculated here instead of from CAProtoShape because for that to
	 * be possible, a reference to the CA has to be stored in each shape. When
	 * there are thousands of shapes, this extra memory use can become
	 * significant.
	 * 
	 * @return Average colour.
	 */
	protected Color getShapeAverageColour(CAProtoShape shape) {
		synchronized (shape) {
			if (shape.getValidate()) {
				Color[] colours = new Color[shape.getAreaCells().size()];
				for (int i = 0; i < colours.length; i++) {
					colours[i] = getColour(shape.getAreaCells().get(i));
				}
				shape.setColour(ColourCompare.averageColour(colours));
			}
			return shape.getColour();
		}
	}

	/**
	 * Finds outline cells of shapes.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void findOutlines(CACell cell) {
		List<CACell> neighbourhood = cell.getNeighbourhood();
		for (CACell neighbour : neighbourhood) {
			if (neighbour != cell && neighbour != paddingCell) {
				CAProtoShape shape = getShape(cell);
				if (shape != getShape(neighbour)) {
					/*
					 * Makes a copy of the cell, so that this CA's cells
					 * continue to use a standard neighbourhood, then expands
					 * the outlineCell's neighbourhood.
					 */
					// CACell outlineCell = new CACell(cell.getCoordinates(),
					// CACell.INACTIVE, meetOutlineNeighbours(cell));
					cell.setNeighbourhood(meetOutlineNeighbours(cell));
					shape.addOutlineCell(cell);

					return;
				}
			}
			/*
			 * Note that the shape's areaCell collection already contains this
			 * cell, so do not add it again.
			 */
		}
	}

	/**
	 * Gets the list of shapes found.
	 * 
	 * @return List of shapes found.
	 */
	public List<CAShape> getShapes() {
		return shapes;
	}
}
