package ca.shapedetector.blob;

import graphics.SDPanelTheme;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import java.util.List;

import ca.Cell;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.AbstractShape;
import ca.shapedetector.shapes.UnknownShape;

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

	/**
	 * The boundary rectangle of this blob.
	 */
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
	 * @see CAShaped::shapeTable
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
	 * Places outline cells in clockwise sequence, with pseudo-random starting
	 * position along the shape's top boundary (depending on which cell was
	 * added to the list first).
	 * <p>
	 * Notice that this forms a closed loop of the shape's outside, so that it
	 * automatically disregards any other enveloped shapes. This does not
	 * however, do anything to add those shapes' areaCells to the shape
	 * enveloping them.
	 */
	public void arrangeOutlineCells() {
		/* For debugging */
		// if (ShapeDetector.debug) {
		// display(outlineCells);
		// }

		Cell<V> first = firstOutlineCell();
		if (first == null) {
			return;
		} else {
			synchronized (outlineCells) {
				LoopFinder<V> loopFinder = new LoopFinder<V>(outlineCells);
				outlineCells = loopFinder.getLoop(first);
			}
			/* For debugging */
			// if (ShapeDetector.debug) {
			// display(outlineCells);
			// }
		}
	}

	/**
	 * For debugging. Displays a blob made of the specified cells.
	 * 
	 * @param cells
	 */
	public static void display(final List<Cell<?>> cells) {
		graphics.ShapeFrame.setTheme(SDPanelTheme.DEFAULT);
		SDPath path = new SDPath();
		path.addCells(cells);
		AbstractShape shape = new UnknownShape(path);
		graphics.ShapeFrame.reset(shape);
		graphics.ShapeFrame.display(shape);
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
	protected Cell<V> firstOutlineCell() {
		List<Cell<V>> cells = outlineCells;
		for (Cell<V> cell : cells) {
			int[] coordinates = cell.getCoordinates();
			/* Looks at row along top boundary: */
			if (coordinates[1] == bounds.getMinY()) {
				return cell;
			}
		}
		return null;
	}

	/**
	 * Gets a boolean signifying whether this blob should attempt to identify
	 * itself.
	 * 
	 * @return
	 */
	public boolean isInsignificant() {
		synchronized (outlineCells) {
			return outlineCells == null || outlineCells.size() < 18
					|| bounds.getWidth() < 4 || bounds.getHeight() < 4;
		}
	}

	/**
	 * Calculate the center of gravity of this blob. This does not work to find
	 * the centroid of shapes that surround other shapes.
	 * 
	 * @return
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