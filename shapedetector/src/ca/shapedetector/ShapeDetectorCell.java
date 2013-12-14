package ca.shapedetector;

import java.awt.Color;

import ca.CAModel;

import graphics.ColourCompare;

/**
 * Gathers together cells of similar colour.
 * @author Sean
 * @bug Does not work as intended yet. There seem to be problems with merging shapes
 * concurrently.
 */
public class ShapeDetectorCell extends CACellShaped {
	/**
	 * Colour that outline cells turn to when they become inactive, that is the
	 * foreground colour of the output image.
	 */
	public final static Color OUTLINE_COLOUR = new Color(0, 0, 0);

	public ShapeDetectorCell(int x, int y, CAModel caModel) {
		super(x, y, caModel);
	}

	/**
	 * 
	 * On the 1st pass, finds shapes (groups of cells of similar colour).
	 * <p>
	 * On the 2nd pass, to find the edge cells, checks whether cell neighbours
	 * any cells belonging to a different shape.
	 */
	@Override
	public void applyRule() {
		for (int i = 0; i < neighbourhoodSize; i++) {
			if (neighbours[i] == paddingCell) {
				break;
			}
			CACellShaped neighbour = (CACellShaped) neighbours[i];
			if (caModel.getPasses() == 0) {
				float difference = ColourCompare.getDifference(getColour(),
						neighbour.getColour());
				if (difference < caModel.getEpsilon()) {
					merge(neighbour);
				}
				setState(ACTIVE);
			} else if (shape != neighbour.getShape()) {
				setOutline();
			} else {
				setArea();
			}
		}
	}

	/**
	 * Make this cell an outline cell (black).
	 * <p>
	 * Note that the apparent thickness of edges is irrelevant. Each shape's
	 * outline is determined by a single layer of cells and this is ensured by
	 * the algorithm.
	 */
	public void setOutline() {
		setColour(OUTLINE_COLOUR);
		setState(INACTIVE);
		shape.addOutlineCell(this);
	}

	/**
	 * Makes this cell an area cell (white).
	 * <p>
	 * Note that the shape's areaCell collection already contains this cell, so
	 * do not add it again.
	 */
	public void setArea() {
		setColour(QUIESCENT_COLOUR);
		setState(INACTIVE);
	}

}
