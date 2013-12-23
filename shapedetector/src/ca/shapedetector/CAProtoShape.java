package ca.shapedetector;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import ca.CACell;

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

	/** Average colour of shape's area cells. */
	protected Color colour;
	/** Signals that shape should update its averageColour. */
	protected boolean validate;

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
}

// /**
// * Draws the shape. Subclasses could extend this.
// *
// * @param graphics
// * Canvas to draw on.
// */
// public void drawArea() {
// graphics.setColor(fillColour); // protoShape.getColour()
// for (CACell cell : protoShape.getAreaCells()) {
// int[] coordinates = cell.getCoordinates();
// graphics.fillRect(coordinates[0], coordinates[1], 1, 1);
// }
// graphics.setColor(outlineColour);
// for (CACell cell : protoShape.getOutlineCells()) {
// int[] coordinates = cell.getCoordinates();
// graphics.fillRect(coordinates[0], coordinates[1], 1, 1);
// }
// }
//
// /**
// * Draws a cross at the centroid of the shape in the specified colour.
// *
// * @param graphics
// * Canvas to draw on.
// */
// public void drawCentroid() {
// graphics.setColor(centroidColour);
// int[] centroid = protoShape.getCentroid();
//
// graphics.drawLine(centroid[0] - 2, centroid[1] - 2, centroid[0] + 2,
// centroid[1] + 2);
// graphics.drawLine(centroid[0] - 2, centroid[1] + 2, centroid[0] + 2,
// centroid[1] - 2);
// }
//
// /**
// * Draws a descriptive label of the shape.
// *
// * @param graphics
// * Canvas to draw on.
// */
// public void drawLabel() {
// int[] centroid = protoShape.getCentroid();
//
// drawString(getClass().getSimpleName(), centroid[0], centroid[1] - 10);
// drawString(getStats(), centroid[0], centroid[1] + 10);
// }
//
// /**
// * Draws a string.
// *
// * @param graphics
// * Canvas to draw on.
// */
// public void drawString(String string, int x, int y) {
// graphics.setColor(labelColour);
// graphics.setFont(font);
// FontMetrics metrics = graphics.getFontMetrics();
//
// int ws = metrics.stringWidth(string);
// int hs = metrics.getDescent();
//
// graphics.drawString(string, (int) (x - ws / 2.0), (float) (y + hs));
// }

// /**
// * A sequence of numbers that describe the angles of tangent lines to the
// * outline of the shape, relative to the x-axis, in degrees. (This way we
// * avoid the added complication of vertical asymptotes). Degrees may be more
// * useful in this case due to rounding errors when computing in radians.
// * Since the tangents will only be multiples of 45deg, we can store them as
// * integers. Integers also require less storage space and are faster to
// * compute.
// */
// protected List<Integer> tangentLines;

// /**
// * Gets the collection of integers describing tangent lines to the shape
// * outline.
// *
// * @return Collection of outline cells.
// */
// public List<Integer> getTangentLines() {
// return tangentLines;
// }
//
// /**
// * Calculates the gradient for each of this shape's outline cells.
// */
// public void calculateTangents() {
// tangentLines = new ArrayList<Integer>(outlineCells.size());
//
// Iterator<CACell> previousIterator = outlineCells.iterator();
// Iterator<CACell> currentIterator = outlineCells.iterator();
//
// CACell previous = previousIterator.next();
// currentIterator.next();
// CACell current = currentIterator.next();
//
// int tangent = getTangent(outlineCells.get(outlineCells.size() - 1),
// previous);
// tangentLines.add(tangent);
//
// while (currentIterator.hasNext()) {
// tangent = getTangent(previous, current);
// tangentLines.add(tangent);
//
// previous = previousIterator.next();
// current = currentIterator.next();
// }
// }
//
// /**
// * Gets the angle formed between two adjacent cells.
// *
// * @param previous
// * @param current
// * @return
// */
// protected int getTangent(CACell previous, CACell current) {
// int[] previousCoordinates = previous.getCoordinates();
// int[] currentCoordinates = current.getCoordinates();
//
// int deltaX = currentCoordinates[0] - previousCoordinates[0];
// int deltaY = currentCoordinates[1] - previousCoordinates[1];
//
// switch (deltaX) {
// case -1:
// switch (deltaY) {
// case -1:
// return 225;
// case 0:
// return 180;
// case 1:
// return 135;
// }
// break;
// case 0:
// switch (deltaY) {
// case -1:
// return 90;
// case 0:
// throw new RuntimeException(
// "Error computing tangent to outline cell.");
// case 1:
// return 270;
// }
// break;
// case 1:
// switch (deltaY) {
// case -1:
// return 315;
// case 0:
// return 0;
// case 1:
// return 45;
// }
// break;
// }
// /*
// * If this point is reached, the current cell is not adjacent to the
// * previous one...
// */
// return -1;
// }

// /**
// * Gets the dimensions of this protoShape, for example its width and height.
// *
// * @return The dimensions of this protoShape.
// */
// public int[] getDimensions() {
// int[] dimensions = new int[boundaries[0].length];
// for (int i = 0; i < dimensions.length; i++) {
// dimensions[i] = boundaries[1][i] - boundaries[0][i] + 1;
// }
// return dimensions;
// }
//
// /**
// * Gets this protoShape's centroid.
// *
// * @return The centroid's coordinates.
// */
// public int[] getCentroid() {
// int[] centroid = new int[boundaries[0].length];
// for (int i = 0; i < centroid.length; i++) {
// centroid[i] = (boundaries[1][i] + boundaries[0][i]) / 2;
// }
// return centroid;
// }