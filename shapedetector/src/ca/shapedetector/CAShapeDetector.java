package ca.shapedetector;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import std.Picture;

import ca.CACell;
import ca.shapedetector.shapes.Rectangle;
import ca.shapedetector.shapes.UnknownShape;

/**
 * Finds shapes in an image.
 * <p>
 * On the 1st pass, finds the edges, ensuring that edges are closed loops, and
 * in so doing also groups cells together into shapes.
 * <p>
 * On the 2nd pass, the outlines of shapes are found.
 * <p>
 * TODO: On the 3rd pass, insignificant shapes are assimilated into neighbouring
 * shapes.
 * <p>
 * On the 4th pass, shapes are requested to identify themselves (as circles,
 * squares and so on).
 * <p>
 * Note that the apparent thickness of edges is irrelevant. Each shape's outline
 * is determined by a single layer of cells and this is ensured by the
 * algorithm.
 * 
 * @author Sean
 */
public class CAShapeDetector extends CAShaped {
	/**
	 * Colour that outline cells turn to when they become inactive, that is the
	 * foreground colour of the output image.
	 */
	public final static Color OUTLINE_COLOUR = new Color(0, 0, 0);
	/**
	 * Shapes with areas smaller than this will be assimilated into larger
	 * shapes.
	 */
	protected int minArea = 25;

	public CAShapeDetector(float epsilon) {
		super(epsilon);
		/* TODO: Add more shapes. */
		CAShape.addShape(new Rectangle());
	}

	@Override
	public Picture apply(Picture picture) {
		Picture output = super.apply(picture);
		output = pointOutShapes(output);
		return output;
	}

	@Override
	protected void endPass() {
		super.endPass();

		switch (passes) {
		case 1:
			/* Pass 3 gets skipped until it gets fixed. */
		case 3:
			List<CAShape> shapes = new ArrayList<CAShape>();
			for (CAShape shape : this.shapes) {
				shapes.add(shape.identify());
			}
			this.shapes = shapes;
			break;
		}
	}

	public Picture pointOutShapes(Picture picture) {
		graphics = pictureAfter.getImage().getGraphics();
		System.out.println("Number of shapes: " + shapes.size());
		System.out.println("Detected shapes: ");
		for (CAShape shape : shapes) {
			/* using instanceof does not seem to work here. */
			if (shape.getClass() != UnknownShape.class) {
				System.out.println(shape);
				shape.setColour(new Color(240, 250, 250));
				drawShape(shape, Color.green);
				drawLabel(shape, Color.blue);
				drawCentroid(shape, Color.red);
			}
		}
		return picture;
	}

	/**
	 * Draws shape with specified colours.
	 * 
	 * @param shape
	 *            The shape to draw.
	 * @param outlineColour
	 *            Outline colour.
	 * @param fillColour
	 *            Fill colour.
	 */
	public void drawShape(CAShape shape, Color outlineColour) {
		for (CACell cell : shape.getAreaCells()) {
			setColour(cell, shape.getColour());
		}
		for (CACell cell : shape.getOutlineCells()) {
			setColour(cell, outlineColour);
		}
	}

	/**
	 * Draws a descriptive label of a shape in the specified colour.
	 * 
	 * @param shape
	 *            The shape to draw.
	 * @param colour
	 *            Text colour.
	 */
	public void drawLabel(CAShape shape, Color colour) {
		graphics.setColor(colour);
		graphics.drawString(shape.getClass().getSimpleName(),
				shape.centroidX(), shape.centroidY() - 5);

		graphics.drawString("w=" + shape.width() + ", h=" + shape.height(),
				shape.centroidX(), shape.centroidY() + 15);
	}

	/**
	 * Draws the centroid of the shape in the specified colour.
	 * 
	 * @param shape
	 *            Shape of which to draw the centroid.
	 * @param colour
	 *            Centroid colour.
	 */
	public void drawCentroid(CAShape shape, Color colour) {
		setColour(getCell(shape.centroidX() - 1, shape.centroidY() - 1), colour);
		setColour(getCell(shape.centroidX() + 1, shape.centroidY() - 1), colour);
		setColour(getCell(shape.centroidX(), shape.centroidY()), colour);
		setColour(getCell(shape.centroidX() - 1, shape.centroidY() + 1), colour);
		setColour(getCell(shape.centroidX() + 1, shape.centroidY() + 1), colour);
	}

	/**
	 * Updates the specified cell.
	 * <p>
	 * On the 1st pass, finds shapes (groups of cells of similar colour).
	 * <p>
	 * On the 2nd pass, to find the edge cells, checks whether cell neighbours
	 * any cells belonging to a different shape.
	 */
	@Override
	public void updateCell(CACell cell) {
		super.updateCell(cell);
		CACell[] neighbourhood = cell.getNeighbourhood();

		/**
		 * On the 3rd pass, this gives the least difference to a neighbouring
		 * shape.
		 */
		float minDifference = 2f;
		/**
		 * On the 3rd pass, if this cell's shape is smaller than the minimum
		 * size, this cell's shape will be assimilated into the superiorCell's
		 * shape.
		 */
		CACell superiorCell = new CACell();

		for (int i = 0; i < neighbourhoodSize; i++) {
			CACell neighbour = neighbourhood[i];
			if (neighbour == cell || neighbour == paddingCell) {
				continue;
			}
			float difference;
			switch (passes) {
			case 0:
				/*
				 * During the 1st pass, cells of similar colour are merged
				 * together into shapes.
				 */
				difference = ColourCompare.getDifference(getColour(cell),
						getColour(neighbour));
				if (difference < epsilon) {
					mergeCells(cell, neighbour);
				}
				active = true;
				// cell.setState(CACell.ACTIVE);
				break;
			case 1:
				/*
				 * During the 2nd pass, the outline cells are separated from the
				 * shapes' area cells.
				 */
				if (getShape(cell) != getShape(neighbour)) {
					getShape(cell).addOutlineCell(cell);
				}
				/*
				 * Note that the shape's areaCell collection already contains
				 * this cell, so do not add it again.
				 */
				active = true;
				// cell.setState(CACell.ACTIVE);
				break;
			case 2:
				/*
				 * During the 3rd pass, shapes smaller than the minimum size are
				 * assimilated into the neighbouring shape most similar to this
				 * one.
				 */
				if (getShape(cell).getArea() >= minArea) {
					return;
				}
				CAShape neighbouringShape = getShape(neighbour);
				if (getShape(cell) != neighbouringShape) {
					Color colour1 = getShapeAverageColour(getShape(cell));
					Color colour2;
					synchronized (neighbouringShape) {
						colour2 = getShapeAverageColour(neighbouringShape);
					}
					difference = ColourCompare.getDifference(colour1, colour2);
					if (difference < minDifference) {
						minDifference = difference;
						superiorCell = neighbour;
					}
				}
				break;
			}
		}
		/*
		 * On the 3rd pass, when this cell's shape is found to be insignificant,
		 * this shape is merged with the neighbouring shape that is most similar
		 * to it.
		 */
		if (passes == 2 && minDifference < 2f) {
			mergeCells(cell, superiorCell);
			// cell.setState(CACell.INACTIVE);
		}
		// System.out.println(cell);
	}
}
