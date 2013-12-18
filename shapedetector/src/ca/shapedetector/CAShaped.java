package ca.shapedetector;

import graphics.ColourCompare;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedList;
import java.util.List;

import std.Picture;
import ca.CA;
import ca.CACell;

/**
 * A cellular automaton that maintains groups of cells attached to CAShapes.
 * Should be extended by a subclass.
 * 
 * @author Sean
 */
public class CAShaped extends CA {
	/** Table mapping cells to shapes. */
	protected CAShape[][] shapeAssociations;
	/** List of unique shapes. */
	protected List<CAShape> shapes;

	// public long time1;
	// public long time2;
	// public long time3;

	/* Used for more advanced drawing */
	public Graphics graphics;

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
		/*
		 * LinkedList performs much better than ArrayList with its remove
		 * method. Might have to make this Collections.synchronizedList(...);
		 */
		shapes = new LinkedList<CAShape>();
		/*
		 * Would it be better to do this later in parallel? Doesn't seem to take
		 * much time anyway.
		 */
		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[0].length; y++) {
				CAShape shape = new CAShape(getCell(x, y));
				shapeAssociations[x][y] = shape;
				shapes.add(shape);
			}
		}
	}

	@Override
	protected void endPass() {
		super.endPass();

		// System.out.println("MERGING TIME 1: " + time1);
		// System.out.println("MERGING TIME 2: " + time2);
		// System.out.println("MERGING TIME 3: " + time3);
		// time1 = 0;
		// time2 = 0;
		// time3 = 0;
	}

	/**
	 * Gets the average colour of a shape. Used on the 3rd pass.
	 * <p>
	 * This is calculated here instead of from CAShape because for that to be
	 * possible, a reference to the CA has to be stored in each shape. When
	 * there are thousands of shapes, this extra memory use can become
	 * significant.
	 * 
	 * @return Average colour.
	 */
	public Color getShapeAverageColour(CAShape shape) {
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
	public List<CAShape> getShapes() {
		return shapes;
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
	 * @see CAShapeMergerThread
	 * @param shape1
	 *            1st shape to merge with.
	 * @param shape2
	 *            2st shape to merge with.
	 */
	protected void mergeShapes(CAShape shape1, CAShape shape2) {
		if (shape1 == shape2) {
			return; /* NB */
		}
		// Stopwatch stopwatch = new Stopwatch();
		synchronized (shape1) {
			synchronized (shape2) {
				/*
				 * Improves efficiency by merging the smaller shape into the
				 * larger shape.
				 */
				CAShape newShape;
				CAShape oldShape;
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
				// time1 += stopwatch.time();
				// stopwatch.start();
				shapes.remove(oldShape);
				// time2 += stopwatch.time();
				// stopwatch.start();
				newShape.merge(oldShape);
				// time3 += stopwatch.time();
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
}