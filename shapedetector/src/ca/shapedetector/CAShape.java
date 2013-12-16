package ca.shapedetector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import bag.Bag;

import ca.CACell;

/**
 * A shape made up of CACells.
 * 
 * @author Sean
 */
public class CAShape implements Comparable<CAShape> {
	/**
	 * Collection of cells that make up this shape.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	List<CACell> areaCells;
	/**
	 * Collection of cells on the perimeter of this shape. This is a subset of
	 * areaCells.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	List<CACell> outlineCells;

	/** The top y-coordinate of this shape. */
	int top;
	/** The bottom y-coordinate of this shape. */
	int bottom;
	/** The left x-coordinate of this shape. */
	int left;
	/** The top x-coordinate of this shape. */
	int right;

	/** Average colour of shape's area cells. */
	protected Color colour;
	/** Signals that shape should update its averageColour. */
	protected boolean validate;

	/**
	 * Singleton constructor.
	 */
	public CAShape() {
	}

	/**
	 * Creates a new shape associated with the specified cell. Assumes that the
	 * cell is mapped to this shape.
	 * <p>
	 * Shape-finding performance could be improved dramatically by using a
	 * linked list implementation that offers O(1) time for the addAll method.
	 * 
	 * @see CAShaped::shapeTable.
	 * @see Bag
	 * @param cell
	 *            A cell that is to belong to the shape.
	 */
	public CAShape(CACell cell) {
		areaCells = Collections.synchronizedList(new ArrayList<CACell>()); // Bag
		outlineCells = Collections.synchronizedList(new ArrayList<CACell>());
		int[] coordinates = cell.getCoordinates();
		left = right = coordinates[0];
		top = bottom = coordinates[1];
		areaCells.add(cell);
		validate = true;
	}

	/**
	 * Transfers all cells from the specified shape to the current shape.
	 * <p>
	 * Updates the shape boundaries at the same time, since this takes almost no
	 * time to do.
	 * 
	 * @param shape
	 *            Shape to merge with.
	 */
	public void merge(CAShape shape) {
		validate = true;

		if (shape.left < left) {
			left = shape.left;
		}
		if (shape.right > right) {
			right = shape.right;
		}

		if (shape.top < top) {
			top = shape.top;
		}
		if (shape.bottom > bottom) {
			bottom = shape.bottom;
		}
		areaCells.addAll(shape.getAreaCells());
		/*
		 * It is necessary to free up memory or there will soon be no space left
		 * on the heap.
		 */
		shape.destroy();
	}

	/**
	 * Add a cell to the collection of cells that make up the outline of this
	 * shape.
	 * 
	 * @param cell
	 *            Cell to add.
	 */
	public void addOutlineCell(CACell cell) {
		outlineCells.add(cell);
	}

	/**
	 * Gets the top y-coordinate of this shape, that is the minimum y-coordinate
	 * of the cells making up this shape.
	 * 
	 * @return Shape's top y-coordinate.
	 */
	public int top() {
		return top;
	}

	/**
	 * Gets the bottom y-coordinate of this shape, that is the maximum
	 * y-coordinate of the cells making up this shape.
	 * 
	 * @return Shape's top y-coordinate.
	 */
	public int bottom() {
		return bottom;
	}

	/**
	 * Gets the top x-coordinate of this shape, that is the minimum x-coordinate
	 * of the cells making up this shape.
	 * 
	 * @return Shape's top x-coordinate.
	 */
	public int left() {
		return left;
	}

	/**
	 * Gets the bottom x-coordinate of this shape, that is the maximum
	 * x-coordinate of the cells making up this shape.
	 * 
	 * @return Shape's top x-coordinate.
	 */
	public int right() {
		return right;
	}

	/**
	 * Gets this shape's width.
	 * 
	 * @return Shape's width.
	 */
	public int width() {
		return right - left;
	}

	/**
	 * Gets this shape's height.
	 * 
	 * @return Shape's height.
	 */
	public int height() {
		return bottom - top;
	}

	/**
	 * Gets the x-coordinate of this shape's centroid.
	 * 
	 * @return Centroid's x-cooordinate.
	 */
	public int centroidX() {
		return (left + right) / 2;
	}

	/**
	 * Gets the y-coordinate of this shape's centroid.
	 * 
	 * @return Centroid's y-cooordinate.
	 */
	public int centroidY() {
		return (top + bottom) / 2;
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
	 * @return Perimeter of the shape.
	 */
	public int getArea() {
		return areaCells.size();
	}

	/**
	 * Gets the length of the perimeter, that is the number of cells that form
	 * the outline of the shape.
	 * 
	 * @return Perimeter of the shape.
	 */
	public int getPerimeter() {
		return outlineCells.size();
	}

	public String toString() {
		return "(Shape) [hash: " + hashCode() + ", area: " + getArea() + "]";
		// return "(Shape) [area: " + getArea() + ", left: " + left + ", top: "
		// + top + ", right: " + right + ", bottom: " + bottom + "]\n";
	}

	/**
	 * Places shapes in order of area size. Guarantees that shapes of identical
	 * size can coexist in a set of unique shapes.
	 */
	@Override
	public int compareTo(CAShape arg0) {
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
}