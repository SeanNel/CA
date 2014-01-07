package ca.shapedetector.blob;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import ca.Cell;
import ca.concurrency.Updatable;

/**
 * A blob made up of CACells. TODO: extend CACell (to take advantage of
 * CACellThreads etc)
 * 
 * @author Sean
 */
public class Blob implements Comparable<Blob>, Updatable {
	/**
	 * Collection of cells that make up this shape.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	protected List<Cell> areaCells;
	/**
	 * Collection of cells on the perimeter of this shape. This is a subset of
	 * areaCells.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	protected List<Cell> outlineCells;

	/**
	 * The boundary coordinates of this shape (a row each for minima and maxima,
	 * a column for each axis, e.g. x and y).
	 */
	protected int[][] boundaries;

	/**
	 * Singleton constructor.
	 */
	public Blob() {
	}

	/**
	 * Creates a new shape associated with the specified cell. Assumes that the
	 * cell is mapped to this shape.
	 * 
	 * @see CAShaped::shapeTable
	 * @param cell
	 *            A cell that is to belong to the shape.
	 */
	public Blob(Cell cell) {
		areaCells = Collections.synchronizedList(new LinkedList<Cell>());
		// newArrayList<CACell>()
		outlineCells = Collections.synchronizedList(new LinkedList<Cell>());
		areaCells.add(cell);

		int[] coordinates = cell.getCoordinates();
		boundaries = new int[2][coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			boundaries[0][i] = boundaries[1][i] = coordinates[i];
		}
	}

	/**
	 * Transfers all cells from the specified blob to the current blob.
	 * <p>
	 * Updates the boundaries at the same time, since this takes almost no time
	 * to do.
	 * 
	 * @param blob
	 *            blob to merge with.
	 */
	public void merge(Blob blob) {
		for (int i = 0; i < boundaries.length; i++) {
			if (blob.boundaries[0][i] < boundaries[0][i]) {
				boundaries[0][i] = blob.boundaries[0][i];
			}
			if (blob.boundaries[1][i] > boundaries[1][i]) {
				boundaries[1][i] = blob.boundaries[1][i];
			}
		}

		areaCells.addAll(blob.getAreaCells());
		/*
		 * It is necessary to free up memory or there will soon be no space left
		 * on the heap.
		 */
		blob.destroy();
	}

	/**
	 * Add a cell to the collection of cells that make up the outline of this
	 * blob.
	 * 
	 * @param cell
	 *            Cell to add.
	 */
	public void addOutlineCell(Cell cell) {
		outlineCells.add(cell);
	}

	/**
	 * Gets the boundary coordinates of this blob.
	 * 
	 * @return A row for each axis, e.g. x and y, with columns for minima and
	 *         maxima.
	 */
	public int[][] getBoundaries() {
		return boundaries;
	}

	/**
	 * Gets the collection of cells that form the shape.
	 * 
	 * @return Collection of area cells.
	 */
	public List<Cell> getAreaCells() {
		return areaCells;
	}

	/**
	 * Gets the collection of cells that form the outline of the shape. This is
	 * a subset of areaCells.
	 * 
	 * @return Collection of outline cells.
	 */
	public List<Cell> getOutlineCells() {
		return outlineCells;
	}

	/**
	 * Gets the area in cells squared, that is the number of cells that make up
	 * the shape.
	 * 
	 * @return Area of the shape.
	 */
	public int getArea() {
		if (areaCells == null) {
			return 0;
		} else {
			return areaCells.size();
		}
	}

	public String toString() {
		return "(" + this.getClass().getSimpleName() + ") ["
				+ arrayToString(boundaries[0]) + ":"
				+ arrayToString(boundaries[1]) + "]";
	}

	protected String arrayToString(int[] array) {
		if (array.length == 0) {
			return "";
		}

		String str = "" + array[0];
		for (int i = 1; i < array.length; i++) {
			str += ", " + array[i];
		}
		return str;
	}

	/** Attempt to free memory allocated to this shape. */
	public void destroy() {
		areaCells.clear();
		outlineCells.clear();
		boundaries = null;
		areaCells = null;
		outlineCells = null;
	}

	/**
	 * Places outline cells in clockwise sequence, with pseudo-random starting
	 * position along the shape's top boundary (depending on which cell was
	 * added to the list first).
	 * <p>
	 * Notice that this forms a closed loop of the shape's outside, so that it
	 * automatically disregards any other enveloped shapes. This does not
	 * however, do anything to add those shapes' areaCells to the shape
	 * enveloping them.
	 * <p>
	 * If areas are to be used for comparison, we should also add those
	 * areaCells where appropriate. This also means that each cell would now
	 * possibly be mapped to more than one shape.
	 */
	public void arrangeOutlineCells() {
		Cell first = firstOutlineCell();
		if (first == null) {
			return;
		} else {
			LoopFinder loopFinder = new LoopFinder(outlineCells);
			outlineCells = loopFinder.getLoop(first);
		}
	}

	/**
	 * Finds a cell along the top boundary.
	 * <p>
	 * It is not difficult to ensure that the top-left cell is selected, but any
	 * one at the top boundary will work, so we'll just pick one, because it's
	 * faster that way.
	 * 
	 * @return The '1st' cell to start the loop of outline cells.
	 */
	protected Cell firstOutlineCell() {
		for (Cell cell : outlineCells) {
			int[] coordinates = cell.getCoordinates();
			/* Looks at row along top boundary: */
			if (coordinates[1] == boundaries[0][1]) {
				return cell;
			}
		}
		return null;
	}

	/**
	 * Places shapes in order of area size. Guarantees that shapes of identical
	 * size can coexist in a set of unique shapes.
	 */
	@Override
	public int compareTo(Blob arg0) {
		if (this == arg0) {
			return 0;
		} else if (getArea() >= arg0.getArea()) {
			return 1;
		} else {
			return -1;
		}
	}

	public boolean isInsignificant() {
		int width = boundaries[1][0] - boundaries[0][0];
		int height = boundaries[1][1] - boundaries[0][1];
		return outlineCells == null || outlineCells.size() < 18 || width < 4
				|| height < 4;
	}

	public double[] calculateCentroid() {
		List<Cell> cells = getAreaCells();
		double left = boundaries[0][0];
		double top = boundaries[0][1];
		double x = 0;
		double y = 0;
		for (Cell cell : cells) {
			int[] coordinates = cell.getCoordinates();
			x += coordinates[0] - left;
			y += coordinates[1] - top;
		}
		x = (x / cells.size()) + left;
		y = (y / cells.size()) + top;
		double[] centroid = { x, y };
		return centroid;
	}
}