package ca.rules.cell;

import exceptions.CAException;
import graphics.ColourCompare;

import java.awt.Color;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;

/**
 * Finds the edges in the image.
 */
public class EdgeFinderRule extends CellRule {
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

	protected final float epsilon;

	public EdgeFinderRule(Lattice<Cell> lattice, Neighbourhood neighbourhoodModel,
			float epsilon) throws CAException {
		super(lattice, neighbourhoodModel);
		this.epsilon = epsilon;
	}

	public void update(Cell cell) {
		List<Cell> neighbourhood = cell.getNeighbourhood();
		for (Cell neighbour : neighbourhood) {
			if (neighbour != cell) {
				float difference = ColourCompare.getDifference(
						lattice.getColour(cell), lattice.getColour(neighbour));
				if (difference > epsilon) {
					lattice.setColour(cell, EDGE_COLOUR);
					return;
				}
			}
		}
		lattice.setColour(cell, QUIESCENT_COLOUR);
	}
}
