package ca.rules.cell;

import exceptions.CAException;
import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;

/**
 * Removes noise from the image.
 * 
 * @author Sean
 */
public class NoiseRemoverRule extends CellRule<Color> {
	protected final double epsilon;

	public NoiseRemoverRule(final Lattice<Color> lattice,
			final Neighbourhood<Color> neighbourhoodModel, final double epsilon)
			throws CAException {
		super(lattice, neighbourhoodModel);
		this.epsilon = epsilon;
	}

	@Override
	public void update(final Cell<Color> cell) throws CAException {
		/*
		 * For some reason this rule performs faster when this is set from here
		 * and not in a separate rule.
		 */
		cell.setNeighbourhood(neighbourhoodModel.gatherNeighbours(cell));

		List<Cell<Color>> neighbourhood = cell.getNeighbourhood();
		List<Color> colours = new ArrayList<Color>(neighbourhood.size());
		Color cellColour = lattice.getState(cell);
		double maxDifference = 0f;

		for (Cell<Color> neighbour : neighbourhood) {
			if (neighbour != cell) {
				Color neighbourColour = lattice.getState(neighbour);
				colours.add(neighbourColour);
				double difference = ColourCompare.getDifference(cellColour,
						neighbourColour);
				if (difference > epsilon) {
					return;
				} else if (difference > maxDifference) {
					maxDifference = difference;
				}
			}
		}

		if (maxDifference < epsilon) {
			/*
			 * Taking the mean value is similar to Gaussian blur, but taking the
			 * median should be more effective at removing different kinds of
			 * noise.
			 */
			Color newColour = ColourCompare.meanColour(colours);
			// Color newColour = ColourCompare.medianColour(cellColour,
			// colours);

			lattice.setState(cell, newColour);
		}
	}
}
