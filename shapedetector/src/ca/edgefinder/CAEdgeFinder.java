package ca.edgefinder;

import graphics.ColourCompare;

import java.awt.Color;

import ca.CA;
import ca.CACell;

/**
 * Finds the edges (points of high contrast) in an image. This class may soon
 * become deprecated in favour of the CAShapeDetector.
 * 
 * @author Sean
 */
public class CAEdgeFinder extends CA {
	public final static Color EDGE_COLOUR = new Color(0, 0, 0);

	public CAEdgeFinder(float epsilon, int r) {
		super(epsilon, r);
	}

	public void updateCell(CACell cell) {
		super.updateCell(cell);
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
		cell.setState(CACell.INACTIVE);
	}

	public void setArea(CACell cell) {
		setColour(cell, QUIESCENT_COLOUR);
		cell.setState(CACell.INACTIVE);
	}
}
