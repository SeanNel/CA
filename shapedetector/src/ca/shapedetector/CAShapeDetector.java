package ca.shapedetector;

import graphics.ColourCompare;

import java.awt.Color;

import ca.CACell;

/**
 * Finds the edges, ensuring that edges are closed loops, while also
 * accomplishing much of the work needed to detect shapes as well.
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
			if (passes == 0) {
				/*
				 * During the 1st pass, cells of similar colour are merged
				 * together into shapes.
				 */
				float difference = ColourCompare.getDifference(getColour(cell),
						getColour(neighbour));
				if (difference < epsilon) {
					mergeCells(cell, neighbour);
				}
				// cell.setState(CACell.ACTIVE);
				active = true;
			} else if (passes == 1) {
				/*
				 * During the 2nd pass, the outline cells are separated from the
				 * shapes' area cells.
				 */
				if (getShape(cell) != getShape(neighbour)) {
					setOutline(cell);
					// cell.setState(CACell.INACTIVE);
				} else {
					setArea(cell);
					// cell.setState(CACell.INACTIVE);
				}
				/* Activate again to enable 3rd pass. 
				 * (It does not work properly yet.) */
				// active = true;
			} else if (passes == 2) {
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
					float difference = ColourCompare.getDifference(colour1,
							colour2);
					if (difference < minDifference) {
						minDifference = difference;
						superiorCell = neighbour;
					}
				}
			}
		}
		if (passes == 2 && minDifference < 2f) {
			mergeCells(cell, superiorCell);
			// cell.setState(CACell.INACTIVE);
		}
		// System.out.println(cell);
	}

	/**
	 * Make this cell an outline cell (black).
	 * <p>
	 * Note that the apparent thickness of edges is irrelevant. Each shape's
	 * outline is determined by a single layer of cells and this is ensured by
	 * the algorithm.
	 */
	public void setOutline(CACell cell) {
		// setColour(cell, OUTLINE_COLOUR);
		getShape(cell).addOutlineCell(cell);
	}

	/**
	 * Makes this cell an area cell (white).
	 * <p>
	 * Note that the shape's areaCell collection already contains this cell, so
	 * do not add it again.
	 */
	public void setArea(CACell cell) {
		// setColour(cell, QUIESCENT_COLOUR);
	}
}
