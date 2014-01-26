package ca.shapedetector.blob;


import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import ca.Cell;

/**
 * A blob made up of CACells.
 * 
 * @author Sean
 */
public class Blob<V> {
	/**
	 * Collection of cells that make up this blob.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	protected List<Cell<V>> areaCells;
	/**
	 * Collection of cells on the perimeter of this blob. This is a subset of
	 * areaCells.
	 * <p>
	 * Not required to be a set, since cells are guaranteed to be unique.
	 */
	protected List<Cell<V>> outlineCells;
	/** The boundary rectangle of this blob. */
	protected Rectangle bounds;

	/**
	 * Singleton constructor.
	 */
	public Blob() {
	}

	/**
	 * Creates a new shape associated with the specified cell. Assumes that the
	 * cell is mapped to this shape.
	 * 
	 * @see ShapeList
	 * @param cell
	 *            A cell that is to belong to the shape.
	 */
	public Blob(final Cell<V> cell) {
		areaCells = new LinkedList<Cell<V>>();
		outlineCells = new LinkedList<Cell<V>>();
		areaCells.add(cell);

		int[] coordinates = cell.getCoordinates();
		bounds = new Rectangle(coordinates[0], coordinates[1], 1, 1);
	}

	/**
	 * Transfers all cells from the specified blob to the current blob.
	 * <p>
	 * Updates the boundaries at the same time, since this takes almost no time
	 * to do.
	 * 
	 * @param blob
	 *            Blob to merge with.
	 */
	public void merge(final Blob<V> blob) {
		bounds = bounds.union(blob.bounds).getBounds();

		synchronized (areaCells) {
			areaCells.addAll(blob.getAreaCells());
		}
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
	public void addOutlineCell(final Cell<V> cell) {
		synchronized (outlineCells) {
			outlineCells.add(cell);
		}
	}

	/**
	 * Sets the collection of cells that form the outline of the shape. This is
	 * used when the outline cells are placed in order.
	 * 
	 * @param cells
	 */
	public void setOutlineCells(final List<Cell<V>> cells) {
		outlineCells = cells;
	}

	/**
	 * Gets the bounding rectangle of this blob.
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * Gets the collection of cells that form the shape.
	 * 
	 * @return Collection of area cells.
	 */
	public List<Cell<V>> getAreaCells() {
		return areaCells;
	}

	/**
	 * Gets the collection of cells that form the outline of the shape. This is
	 * a subset of areaCells.
	 * 
	 * @return Collection of outline cells.
	 */
	public List<Cell<V>> getOutlineCells() {
		return outlineCells;
	}

	/**
	 * Gets the surface area in cells squared, that is the number of cells that
	 * make up the shape.
	 * 
	 * @return Area of the shape.
	 */
	public int getArea() {
		if (areaCells == null) {
			return 0;
		} else {
			synchronized (areaCells) {
				return areaCells.size();
			}
		}
	}

	public String toString() {
		return "(" + this.getClass().getSimpleName() + ") [" + bounds + "]";
	}

	/** Attempts to free memory allocated to this shape. */
	public void destroy() {
		areaCells.clear();
		outlineCells.clear();
		bounds = null;
		areaCells = null;
		outlineCells = null;
	}

	/**
	 * Calculate the center of gravity of this blob. This does not work to find
	 * the centroid of shapes that surround other shapes.
	 * 
	 * @deprecated.
	 */
	public Point2D calculateCentroid() {
		List<Cell<V>> cells = getAreaCells();
		double left = bounds.getMinX();
		double top = bounds.getMinY();
		double x = 0;
		double y = 0;
		for (Cell<V> cell : cells) {
			int[] coordinates = cell.getCoordinates();
			x += coordinates[0] - left;
			y += coordinates[1] - top;
		}
		x = (x / cells.size()) + left;
		y = (y / cells.size()) + top;
		return new Point2D.Double(x, y);
	}
}