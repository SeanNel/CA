package rules.cell;

import graphics.ColourCompare;

import java.awt.Color;

import neighbourhood.Neighbourhood;

import rules.AbstractRule;
import shapedetector.ShapeDetector;

import ca.Cell;
import ca.Lattice;

/**
 * Finds the edges in the image.
 */
public class EdgeFinderRule extends AbstractRule<Cell> {
	protected final Lattice<Color> colourLattice;
	protected final Lattice<Double> doubleLattice;
	protected final Neighbourhood neighbourhood;
	protected final double epsilon;

	public final static Color QUIESCENT_COLOUR = Color.white;
	public final static Color EDGE_COLOUR = Color.gray;

	public EdgeFinderRule(final Lattice<Color> colourLattice,
			final Lattice<Double> doubleLattice,
			final Neighbourhood neighbourhood, final double epsilon) {
		super();
		if (doubleLattice == null) {
			throw new NullPointerException("doubleLattice");
		}
		if (colourLattice == null) {
			throw new NullPointerException("colourLattice");
		}
		if (neighbourhood == null) {
			throw new NullPointerException("neighbourhood");
		}

		this.colourLattice = colourLattice;
		this.doubleLattice = doubleLattice;
		this.neighbourhood = neighbourhood;
		this.epsilon = epsilon;
	}

	@Override
	public void update(Cell cell) throws Exception {
		Color cellColour = colourLattice.getState(cell);

		for (Cell neighbour : neighbourhood.neighbours(cell)) {
			if (neighbour != cell) {
				Color neighbourColour = colourLattice.getState(neighbour);

				double difference = ColourCompare.getDifference(cellColour,
						neighbourColour);
				if (difference > epsilon) {
					doubleLattice.setState(cell, CellStates.ACTIVE);
					if (ShapeDetector.debug) {
						colourLattice.setState(cell, EDGE_COLOUR);
					}
					return;
				}
			}
		}
		doubleLattice.setState(cell, CellStates.QUIESCENT);
		if (ShapeDetector.debug) {
			colourLattice.setState(cell, QUIESCENT_COLOUR);
		}
	}
}
