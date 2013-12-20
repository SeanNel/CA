package ca.shapedetector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import ca.CA;
import ca.CACell;

/**
 * A shape made up of CACells.
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

	/** Average colour of shape's area cells. */
	protected Color colour;
	/** Signals that shape should update its averageColour. */
	protected boolean validate;

	/** List of recognizable shapes. */
	protected static Set<CAProtoShape> recognizedShapes = new HashSet<CAProtoShape>();

	/**
	 * Singleton constructor.
	 */
	public CAProtoShape() {
	}

	/**
	 * Creates a new shape associated with the specified cell. Assumes that the
	 * cell is mapped to this shape.
	 * 
	 * @see CAShaped::shapeTable.
	 * @param cell
	 *            A cell that is to belong to the shape.
	 */
	public CAProtoShape(CACell cell) {
		colour = Color.white;
		validate = true;

		areaCells = Collections.synchronizedList(new ArrayList<CACell>());
		outlineCells = Collections.synchronizedList(new ArrayList<CACell>());
		areaCells.add(cell);

		int[] coordinates = cell.getCoordinates();
		boundaries = new int[2][coordinates.length];
		for (int i = 0; i < coordinates.length; i++) {
			boundaries[0][i] = boundaries[1][i] = coordinates[i];
		}
	}

	/**
	 * Copy constructor.
	 * 
	 * @param shape
	 *            The shape to make a copy of.
	 */
	public CAProtoShape(CAProtoShape protoShape) {
		areaCells = protoShape.areaCells;
		outlineCells = protoShape.outlineCells;
		boundaries = protoShape.boundaries.clone();
		colour = protoShape.colour;
		validate = protoShape.validate;
	}

	/**
	 * Add a shape to the list of recognizable shapes.
	 * 
	 * @param shape
	 *            Shape to add.
	 */
	public static void addShape(CAProtoShape protoShape) {
		recognizedShapes.add(protoShape);
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
		validate = true;

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
	 * Gets the dimensions of this protoShape, for example its width and height.
	 * 
	 * @return The dimensions of this protoShape.
	 */
	public int[] getDimensions() {
		int[] dimensions = new int[boundaries[0].length];
		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i] = boundaries[1][i] - boundaries[0][i] + 1;
		}
		return dimensions;
	}

	/**
	 * Gets this protoShape's centroid.
	 * 
	 * @return The centroid's coordinates.
	 */
	public int[] getCentroid() {
		int[] centroid = new int[boundaries[0].length];
		for (int i = 0; i < centroid.length; i++) {
			centroid[i] = (boundaries[1][i] + boundaries[0][i]) / 2;
		}
		return centroid;
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
		return "(" + this.getClass().getSimpleName() + ") [centroid: "
				+ arrayToString(getCentroid()) + ", dimensions: "
				+ arrayToString(getDimensions()) + "]";
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

	/** Attempt to free memory allocated to this shape. */
	public void destroy() {
		areaCells = null;
		outlineCells = null;
	}

	/**
	 * Gets this shape's average colour.
	 * 
	 * @return Shape's average colour.
	 */
	public Color getColour() {
		return colour;
	}

	/**
	 * Sets this shape's average colour.
	 * 
	 * @param colour
	 *            Colour to set to.
	 */
	public void setColour(Color colour) {
		validate = false;
		this.colour = colour;
	}

	/**
	 * Gets this shape's average colour.
	 * 
	 * @return Boolean value indicating whether shape should revalidate, that is
	 *         whether its average colour should be recalculated.
	 */
	public boolean getValidate() {
		return validate;
	}

	/**
	 * Calculates the gradient for each of this shape's outline cells.
	 */
	public void calculateGradients() {
		// TODO Auto-generated method stub
	}

	/**
	 * Places outline cells in linear sequence, with pseudo-random starting
	 * position.
	 */
	public void orderOutlineCells() {
		if (outlineCells == null || outlineCells.size() < 4) {
			return;
		}

		/* Creates temporary list containing the first outlineCell. */
		ArrayList<CACell> orderedList = new ArrayList<CACell>(
				outlineCells.size());
		orderedList.add(outlineCells.get(0));

		CACell previous = null;
		CACell next = orderedList.get(0);
		
		Iterator<CACell> iterator = outlineCells.iterator();
		iterator.next();
		while (iterator.hasNext()) {
			iterator.next();
			
			next = nextOutlineCell(previous, next);
			previous = orderedList.get(orderedList.size() - 1);
			System.out.println(" :: " + next);
			orderedList.add(next);
		}

		outlineCells = orderedList;

		for (CACell c : outlineCells) {
			System.out.println(c);
		}
	}

	/**
	 * Finds the next outline cell. Assumes the cell's neighbourhood is a 3*3
	 * square. When there is ambiguity, assumes the next cell is above and/or to
	 * the right. Assumes that the current cell is not included in its own
	 * neighbourhood.
	 * 
	 * @param previousCell
	 *            The previous outline cell.
	 * @param currentCell
	 *            The current outline cell.
	 * @return The next outline cell.
	 */
	protected CACell nextOutlineCell(CACell previousCell, CACell currentCell) {
		List<CACell> neighbourhood = currentCell.getNeighbourhood();

		for (CACell neighbour : neighbourhood) {
			/* outlineCells >> hashSet or bst etc? */
			if (neighbour != CA.paddingCell && neighbour != previousCell
					&& outlineCells.contains(neighbour)) {
				return neighbour;
			}
		}
		return null;
	}
}