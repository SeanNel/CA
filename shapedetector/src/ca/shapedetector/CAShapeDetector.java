package ca.shapedetector;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import std.Picture;

import ca.CACell;
import ca.Stopwatch;
import ca.shapedetector.shapes.Rectangle;
import ca.shapedetector.shapes.UnknownShape;

/**
 * Finds shapes in an image.
 * <p>
 * On the (optional) 1st pass, finds the edges. It seems better to do this from
 * a separate CA with r > 1. Time lost seems negligible.
 * <p>
 * On the 2nd pass, finds the outlines, ensuring that outlines are closed loops,
 * and in so doing also groups cells together into shapes.
 * <p>
 * On the 2nd pass, the outlines of shapes are found.
 * <p>
 * On the 3rd pass, insignificant shapes are assimilated into neighbouring
 * shapes. (seems to work)
 * <p>
 * The CA is done. Now outline cells are to calculate their gradients.
 * <p>
 * Then shapes are requested to identify themselves (as circles, squares and so
 * on).
 * <p>
 * Note that the apparent thickness of edges is irrelevant. Each shape's outline
 * is determined by a single layer of cells and this is ensured by the
 * algorithm.
 * 
 * @author Sean
 */
public class CAShapeDetector extends CAShaped {
	/**
	 * Colour that edge cells turn to, that is the foreground colour of the
	 * output image.
	 */
	public final static Color EDGE_COLOUR = new Color(0, 0, 0);
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
		/* Skips integrated edge finding . */
		passes = 1;

		Picture output = super.apply(picture);
		output = pointOutShapes(output);
		return output;
	}

	@Override
	protected void postProcess() {
		Stopwatch stopwatch = new Stopwatch();
		long t1 = 0;
		long t2 = 0;

		List<CAShape> shapes = new ArrayList<CAShape>();
		for (CAShape shape : this.shapes) {
			/* Calculate gradients. */
			stopwatch.start();
			shape.calculateGradients();
			t1 += stopwatch.time();

			/* Identify shapes. */
			stopwatch.start();
			shapes.add(shape.identify());
			t2 += stopwatch.time();
		}
		this.shapes = shapes;

		System.out.println("Calculated gradients: " + t1 + " ms");
		System.out.println("Identified shapes: " + t2 + " ms");
	}

	/**
	 * Displays which shapes were found where.
	 * 
	 * @param picture
	 *            Picture to render to.
	 * @return Rendered picture.
	 */
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

	@Override
	public void updateCell(CACell cell) {
		switch (passes) {
		case 0:
			initCell(cell);
			findEdges(cell);
			active = true;
		case 1:
			/* Necessary to init if pass 1 was skipped. */
			initCell(cell);
			findShapes(cell);
			active = true;
			break;
		case 2:
			findOutlines(cell);
			active = true;
		case 3:
			assimilateInsignificantShapes(cell);
		}
		// System.out.println(cell);
	}

	public void findEdges(CACell cell) {
		CACell[] neighbourhood = cell.getNeighbourhood();
		for (int i = 0; i < neighbourhoodSize; i++) {
			CACell neighbour = neighbourhood[i];
			if (neighbour == cell || neighbour == paddingCell) {
				continue;
			}
			float difference = ColourCompare.getDifference(getColour(cell),
					getColour(neighbour));
			if (difference > epsilon) {
				setEdge(cell);
				return;
			}
		}
		setArea(cell);
	}

	public void setEdge(CACell cell) {
		setColour(cell, EDGE_COLOUR);
	}

	public void setArea(CACell cell) {
		setColour(cell, QUIESCENT_COLOUR);
	}

	/**
	 * Cells of similar colour are merged together into shapes.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void findShapes(CACell cell) {
		CACell[] neighbourhood = cell.getNeighbourhood();

		for (int i = 0; i < neighbourhoodSize; i++) {
			CACell neighbour = neighbourhood[i];
			if (neighbour == cell || neighbour == paddingCell) {
				continue;
			}
			float difference = ColourCompare.getDifference(getColour(cell),
					getColour(neighbour));
			if (difference < epsilon) {
				mergeCells(cell, neighbour);
			}
		}
	}

	/**
	 * Finds outline cells of shapes.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void findOutlines(CACell cell) {
		CACell[] neighbourhood = cell.getNeighbourhood();

		for (int i = 0; i < neighbourhoodSize; i++) {
			CACell neighbour = neighbourhood[i];
			if (neighbour == cell || neighbour == paddingCell) {
				continue;
			}

			if (getShape(cell) != getShape(neighbour)) {
				getShape(cell).addOutlineCell(cell);
			}
			/*
			 * Note that the shape's areaCell collection already contains this
			 * cell, so do not add it again.
			 */
		}
	}

	/**
	 * Assimilates insignificant shapes into neighbouring shapes.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void assimilateInsignificantShapes(CACell cell) {
		/** This gives the least difference to a neighbouring shape. */
		float minDifference = 2f;
		/**
		 * If this cell's shape is smaller than the minimum size, this cell's
		 * shape will be assimilated into the superiorCell's shape.
		 */
		CACell superiorCell = null;

		CACell[] neighbourhood = cell.getNeighbourhood();

		for (int i = 0; i < neighbourhoodSize; i++) {
			CACell neighbour = neighbourhood[i];
			if (neighbour == cell || neighbour == paddingCell) {
				continue;
			}

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
				float difference = ColourCompare
						.getDifference(colour1, colour2);
				if (difference < minDifference) {
					minDifference = difference;
					superiorCell = neighbour;
				}
			}
		}

		/*
		 * When this cell's shape is found to be insignificant, this shape is
		 * merged with the neighbouring shape that is most similar to it.
		 */
		if (minDifference < 2f) {
			mergeCells(cell, superiorCell);
		}
		// cell.setState(CACell.INACTIVE);
	}
}
