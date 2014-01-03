package ca.shapedetector;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Area;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import std.Picture;

import ca.CACell;
import ca.shapedetector.path.SDPath;

/**
 * A ProtoShape made up of CACells.
 * 
 * @author Sean
 */
public class CAProtoShape implements Comparable<CAProtoShape> {
	/**
	 * Collection of cells that make up this shape.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	protected List<CACell> areaCells;
	/**
	 * Collection of cells on the perimeter of this shape. This is a subset of
	 * areaCells.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	protected List<CACell> outlineCells;

	/**
	 * The boundary coordinates of this shape (a row each for minima and maxima,
	 * a column for each axis, e.g. x and y).
	 */
	protected int[][] boundaries;

	/**
	 * Singleton constructor.
	 */
	public CAProtoShape() {
	}

	/**
	 * Creates a new shape associated with the specified cell. Assumes that the
	 * cell is mapped to this shape.
	 * 
	 * @see CAShaped::shapeTable
	 * @param cell
	 *            A cell that is to belong to the shape.
	 */
	public CAProtoShape(CACell cell) {
		areaCells = Collections.synchronizedList(new LinkedList<CACell>()); //new ArrayList<CACell>()
		outlineCells = Collections.synchronizedList(new LinkedList<CACell>());
		areaCells.add(cell);

		int[] coordinates = cell.getCoordinates();
		boundaries = new int[2][coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			boundaries[0][i] = boundaries[1][i] = coordinates[i];
		}
	}

	/**
	 * Transfers all cells from the specified protoShape to the current
	 * protoShape.
	 * <p>
	 * Updates the boundaries at the same time, since this takes almost no time
	 * to do.
	 * 
	 * @param protoShape
	 *            protoShape to merge with.
	 */
	public void merge(CAProtoShape protoShape) {
		for (int i = 0; i < boundaries.length; i++) {
			if (protoShape.boundaries[0][i] < boundaries[0][i]) {
				boundaries[0][i] = protoShape.boundaries[0][i];
			}
			if (protoShape.boundaries[1][i] > boundaries[1][i]) {
				boundaries[1][i] = protoShape.boundaries[1][i];
			}
		}

		areaCells.addAll(protoShape.getAreaCells());
		/*
		 * It is necessary to free up memory or there will soon be no space left
		 * on the heap.
		 */
		protoShape.destroy();
	}

	/**
	 * Add a cell to the collection of cells that make up the outline of this
	 * protoShape.
	 * 
	 * @param cell
	 *            Cell to add.
	 */
	public void addOutlineCell(CACell cell) {
		outlineCells.add(cell);
	}

	/**
	 * Gets the boundary coordinates of this protoShape.
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
	public List<CACell> getAreaCells() {
		return areaCells;
	}

	/**
	 * Gets the collection of cells that form the outline of the shape. This is
	 * a subset of areaCells.
	 * 
	 * @return Collection of outline cells.
	 */
	public List<CACell> getOutlineCells() {
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
		CACell first = firstOutlineCell();
		if (first == null) {
			return;
		} else {
			CALoopFinder loopFinder = new CALoopFinder(outlineCells);
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
	protected CACell firstOutlineCell() {
		for (CACell cell : outlineCells) {
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
	public int compareTo(CAProtoShape arg0) {
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

	public static final Picture debugPicture = new Picture(600, 600);

	public static void clearDebugPicture() {
		Graphics2D graphics = debugPicture.getImage().createGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, debugPicture.width(), debugPicture.height());
	}

	public void display() {
		Area area = SDPath.makeArea(areaCells);
		// area = SDPath.fillGaps(area);

		SDPath path = new SDPath(area);
		path.draw(debugPicture, Color.blue, new Color(255, 255, 0, 20));
		debugPicture.show();
	}

	public double[] calculateCentroid() {
		List<CACell> cells = getAreaCells();
		double left = boundaries[0][0];
		double top = boundaries[0][1];
		double x = 0;
		double y = 0;
		for (CACell cell : cells) {
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