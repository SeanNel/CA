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
public class EdgeFinderRule extends CellRule<Color> {
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

	protected final double epsilon;

	public EdgeFinderRule(final Lattice<Color> lattice,
			final Neighbourhood<Color> neighbourhoodModel, final double epsilon)
			throws CAException {
		super(lattice, neighbourhoodModel);
		this.epsilon = epsilon;
	}

	@Override
	public void update(Cell<Color> cell) {
		List<Cell<Color>> neighbourhood = cell.getNeighbourhood();
		for (Cell<Color> neighbour : neighbourhood) {
			if (neighbour != cell) {
				double difference = ColourCompare.getDifference(
						lattice.getState(cell), lattice.getState(neighbour));
				if (difference > epsilon) {
					lattice.setState(cell, EDGE_COLOUR);
					return;
				}
			}
		}
		lattice.setState(cell, QUIESCENT_COLOUR);
	}
}
