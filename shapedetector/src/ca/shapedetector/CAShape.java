package ca.shapedetector;

import java.util.ArrayList;

/**
 * A shape made up of CACells.
 * 
 * @author Sean
 */
public class CAShape {
	/**
	 * Collection of cells that make up this shape.
	 * <p>
	 * HashSet not needed, cells are guaranteed to be unique when merging.
	 */
	ArrayList<CACellShaped> areaCells;
	/**
	 * Collection of cells on the perimeter of this shape. This is a subset of
	 * areaCells.
	 */
	ArrayList<CACellShaped> outlineCells;

	/** The top y-coordinate of this shape. */
	int top;
	/** The bottom y-coordinate of this shape. */
	int bottom;
	/** The left x-coordinate of this shape. */
	int left;
	/** The top x-coordinate of this shape. */
	int right;

	/**
	 * Creates a new shape associated with the specified cell. Assumes that the
	 * cell's shape reference points to this shape.
	 * 
	 * @param cell
	 *            A cell that is to belong to the shape.
	 */
	public CAShape(CACellShaped cell) {
		areaCells = new ArrayList<CACellShaped>();
		outlineCells = new ArrayList<CACellShaped>();
		top = bottom = cell.getX();
		left = right = cell.getY();
		addAreaCell(cell);
	}

	/**
	 * Transfers all cells from the specified shape to the current shape.
	 * 
	 * @param shape
	 *            Shape to merge with.
	 */
	public void merge(CAShape shape) {
		synchronized (shape) {
			for (CACellShaped cell : shape.getAreaCells()) {
				// synchronized (cell) {
				cell.setShape(this);
				// }
			}

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
		}
	}

	/**
	 * Add a cell to this shape.
	 * 
	 * @param cell
	 *            Cell to add.
	 */
	public synchronized void addAreaCell(CACellShaped cell) {
		areaCells.add(cell);

		int cellX = cell.getX();
		int cellY = cell.getY();

		if (cellX < left) {
			left = cellX;
		}
		if (cellX > right) {
			right = cellX;
		}

		if (cellY < top) {
			top = cellY;
		}
		if (cellY > bottom) {
			bottom = cellY;
		}
	}

	/**
	 * Add a cell to the collection of cells that make up the outline of this
	 * shape.
	 * 
	 * @param cell
	 *            Cell to add.
	 */
	public synchronized void addOutlineCell(CACellShaped cell) {
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
	public ArrayList<CACellShaped> getAreaCells() {
		return areaCells;
	}

	/**
	 * Gets the collection of cells that form the outline of the shape. This is
	 * a subset of areaCells.
	 * 
	 * @return Collection of outline cells.
	 */
	public ArrayList<CACellShaped> getOutlineCells() {
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
		return "(Shape) [area: " + getArea() + "]";
		// return "(Shape) [area: " + area + ", left: " + left + ", top: " + top
		// + ", right: " + right + ", bottom: " + bottom + "]\n";
	}
}