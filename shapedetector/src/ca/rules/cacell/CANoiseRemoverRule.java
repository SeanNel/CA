package ca.rules.cacell;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ca.CA;
import ca.CACell;

public class CANoiseRemoverRule extends CACellRule {

	public CANoiseRemoverRule(CA ca) {
		super(ca);
	}

	public void update(CACell cell) {
		/*
		 * For some reason this rule performs faster when this is set from here
		 * and not in a separate rule.
		 */
		cell.setNeighbourhood(ca.gatherNeighbours(cell));

		List<CACell> neighbourhood = cell.getNeighbourhood();
		List<Color> colours = new ArrayList<Color>(neighbourhood.size());
		Color cellColour = ca.getColour(cell);
		float maxDifference = 0f;

		for (CACell neighbour : neighbourhood) {
			if (neighbour != cell && neighbour != CA.paddingCell) {
				Color neighbourColour = ca.getColour(neighbour);
				colours.add(neighbourColour);
				float difference = ColourCompare.getDifference(cellColour,
						neighbourColour);
				if (difference > ca.getEpsilon()) {
					return;
				} else if (difference > maxDifference) {
					maxDifference = difference;
				}
			}
		}

		if (maxDifference < ca.getEpsilon()) {
			/*
			 * Taking the mean value is similar to Gaussian blur, but taking the
			 * median should be more effective at removing different kinds of
			 * noise.
			 */
			Color newColour = ColourCompare.meanColour(colours);
			// Color newColour = ColourCompare.medianColour(cellColour,
			// colours);

			ca.setColour(cell, newColour);
		}
	}
}
