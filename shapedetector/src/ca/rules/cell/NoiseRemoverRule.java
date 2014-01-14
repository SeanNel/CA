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
public class NoiseRemoverRule extends CellRule {
	protected final float epsilon;

	public NoiseRemoverRule(Lattice<Cell> lattice, Neighbourhood neighbourhoodModel,
			float epsilon) throws CAException {
		super(lattice, neighbourhoodModel);
		this.epsilon = epsilon;
	}

	public void update(Cell cell) {
		/*
		 * For some reason this rule performs faster when this is set from here
		 * and not in a separate rule.
		 */
		cell.setNeighbourhood(neighbourhoodModel.gatherNeighbours(cell));

		List<Cell> neighbourhood = cell.getNeighbourhood();
		List<Color> colours = new ArrayList<Color>(neighbourhood.size());
		Color cellColour = lattice.getColour(cell);
		float maxDifference = 0f;

		for (Cell neighbour : neighbourhood) {
			if (neighbour != cell) {
				Color neighbourColour = lattice.getColour(neighbour);
				colours.add(neighbourColour);
				float difference = ColourCompare.getDifference(cellColour,
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

			lattice.setColour(cell, newColour);
		}
	}
}
