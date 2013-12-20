package ca.rules.cacell;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.List;

import ca.CA;
import ca.CACell;

/**
 * Finds the edges in the image.
 */
public class CAEdgeFinderRule extends CACellRule {
	/**
	 * Colour that cells turn to when they become inactive, that is the
	 * background colour of the output image.
	 */
	public final static Color QUIESCENT_COLOUR = new Color(255, 255, 255);
	/**
	 * Colour that edge cells turn to, that is the foreground colour of the
	 * output image.
	 */
	public final static Color EDGE_COLOUR = new Color(200, 200, 200);

	public CAEdgeFinderRule(CA ca) {
		super(ca);
	}

	public void update(CACell cell) {
		List<CACell> neighbourhood = cell.getNeighbourhood();
		for (CACell neighbour : neighbourhood) {
			if (neighbour != cell && neighbour != CA.paddingCell) {
				float difference = ColourCompare.getDifference(
						ca.getColour(cell), ca.getColour(neighbour));
				if (difference > ca.getEpsilon()) {
					ca.setColour(cell, EDGE_COLOUR);
					return;
				}
			}
		}
		ca.setColour(cell, QUIESCENT_COLOUR);
	}
}
