package ca.shapedetector;

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import std.Picture;
import ca.CACell;
import ca.CA;
import ca.concurrency.CAShapeMergerThread;

/**
 * A cellular automaton that maintains groups of cells attached to CAShapes.
 * Should be extended by a subclass.
 * 
 * @author Sean
 */
public class CAShaped extends CA {
	/** Table mapping cells to shapes. */
	protected CAShape[][] shapeAssociations;
	/** Set of unique shapes. */
	protected Set<CAShape> shapes;
	/** Shapes with areas smaller than this will be ignored. */
	protected int minArea = 16;
	/** Separate thread that merges shapes while cells continue updating. */
	CAShapeMergerThread shapeMergerThread;

	/**
	 * Constructor. Sets neighbourhoodModel to VANNEUMANN_NEIGHBOURHOOD.
	 * 
	 * @param epsilon
	 *            The difference threshold expressed as a fraction. Determines
	 *            how neighbourhood cells affect this cell's state. Low values
	 *            mean that small differences between cells are ignored.
	 */
	public CAShaped(float epsilon) {
		super(epsilon, 1);
		neighbourhoodModel = VANNEUMANN_NEIGHBOURHOOD; /* NB */
	}

	@Override
	public void setPicture(Picture picture) {
		super.setPicture(picture);
		shapeAssociations = new CAShape[picture.width()][picture.height()];
		shapes = Collections.synchronizedSet(new TreeSet<CAShape>());
		/* TODO: do this later in parallel */
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[0].length; y++) {
				CAShape shape = new CAShape(getCell(x, y));
				shapeAssociations[x][y] = shape;
				shapes.add(shape);
			}
		}
	}

	@Override
	public Picture apply(Picture picture) {
		Picture output = super.apply(picture);
		output = pointOutShapes(output);
		return output;
	}

	/**
	 * Display where shapes were found in the picture.
	 * 
	 * @param picture
	 *            Picture to draw to.
	 */
	public Picture pointOutShapes(Picture picture) {
		return picture;
	}

	/**
	 * Assuming that r=1, this is a more efficient method of gathering
	 * neighbours.
	 */
	protected void meetNeighboursVanNeumann(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		CACell[] neighbourhood = new CACell[neighbourhoodSize];
		neighbourhood[0] = getCell(coordinates[0], coordinates[1] - 1);
		neighbourhood[1] = getCell(coordinates[0], coordinates[1] + 1);
		neighbourhood[2] = getCell(coordinates[0] - 1, coordinates[1]);
		neighbourhood[3] = getCell(coordinates[0] + 1, coordinates[1]);

		getCell(coordinates[0], coordinates[1]).setNeighbourhood(neighbourhood);
	}

	/**
	 * Gets the list of shapes found.
	 */
	public Set<CAShape> getShapes() {
		return shapes;
	}

	@Override
	protected void updateModel() {
		shapeMergerThread = new CAShapeMergerThread(this);
		shapeMergerThread.start();
		super.updateModel();
		shapeMergerThread.finish();
		synchronized (shapeMergerThread) {
		}
	}

	/**
	 * Merges two cells' shapes together.
	 * 
	 * @param cell1
	 *            1st cell to merge with.
	 * @param cell2
	 *            2nd cell to merge with.
	 */
	public void mergeCells(CACell cell1, CACell cell2) {
		synchronized (cell1.lock) {
			CAShape shape1 = getShape(cell1);
			synchronized (shape1.lock) {
				synchronized (cell2.lock) {
					mergeShapes(shape1, getShape(cell2));
				}
			}
		}
	}

	/**
	 * Merges 2 shapes together.
	 * 
	 * @see CAShapeMergerThread
	 * @param shape1
	 *            1st shape to merge with.
	 * @param shape2
	 *            2st shape to merge with.
	 * @bug Synchronization problems.
	 */
	public void mergeShapes(CAShape shape1, CAShape shape2) {
		// Stopwatch s = new Stopwatch();
		synchronized (shape1.lock) {
			synchronized (shape2.lock) {
				if (shape1.getAreaCells() == null) {
					throw new RuntimeException(); /* << Synchronization problem */
				}
				synchronized (shapes) {
					shapes.remove(shape1);
				}
				for (CACell cell : shape1.getAreaCells()) {
					setShape(cell, shape2);
				}
				if (shape2.getAreaCells() == null) {
					throw new RuntimeException(); /* << Synchronization problem */
				}
				shape2.merge(shape1);
				shape1.destroy();
			}
		}
		// s.print();
	}

	/**
	 * Merges 2 cell's shapes together later.
	 * 
	 * @param shape
	 *            1st cell to merge with.
	 * @param shape
	 *            2nd cell to merge with.
	 */
	protected void enqueueMerger(CACell cell1, CACell cell2) {
		shapeMergerThread.enqueue(cell1, cell2);
	}

	/**
	 * Gets the shape associated with the specified cell.
	 * 
	 * @param cell
	 *            A cell belonging to a shape.
	 * @return The shape associated with the specified cell.
	 */
	public CAShape getShape(CACell cell) {
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
	public void setShape(CACell cell, CAShape shape) {
		int[] coordinates = cell.getCoordinates();
		shapeAssociations[coordinates[0]][coordinates[1]] = shape;
	}

	/**
	 * Print a summary of the detected shapes.
	 */
	public void printSummary() {
		System.out.println("Number of shapes: " + shapes.size());
		// System.out.println(shapes);
	}
}