package ca.edgefinder;

import java.awt.Color;

import graphics.ColourCompare;
import ca.CACell;
import ca.CAModel;

/**
 * Finds the edges (points of high contrast) in an image. This class may soon
 * become deprecated in favour of the CAShapeDetector.
 * 
 * @author Sean
 */
public class EdgeFinderCell extends CACell {
	public final static Color EDGE_COLOUR = new Color(0, 0, 0);

	public EdgeFinderCell(int x, int y, CAModel caModel) {
		super(x, y, caModel);
	}

	public void applyRule() {
		for (int i = 0; i < neighbourhoodSize; i++) {
			if (neighbours[i] == paddingCell) {
				break;
			}
			float difference = ColourCompare.getDifference(getColour(),
					neighbours[i].getColour());
			if (difference > caModel.getEpsilon()) {
				setEdge();
				return;
			}
		}
		setArea();
	}

	public void setEdge() {
		setColour(EDGE_COLOUR);
		setState(INACTIVE);
	}

	public void setArea() {
		setColour(QUIESCENT_COLOUR);
		setState(INACTIVE);
	}
}
