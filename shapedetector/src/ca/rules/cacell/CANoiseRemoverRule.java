package ca.rules.cacell;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.List;

import ca.CA;
import ca.CACell;

public class CANoiseRemoverRule extends CACellRule {
	
	public CANoiseRemoverRule(CA ca) {
		super(ca);
	}

	public void update(CACell cell) {
		cell.setNeighbourhood(ca.gatherNeighbours(cell));
		
		float maxDifference = 0f;

		List<CACell> neighbourhood = cell.getNeighbourhood();
		Color[] colours = new Color[neighbourhood.size()];
		int i = 0;
		for (CACell neighbour : neighbourhood) {
			if (neighbour == cell || neighbour == CA.paddingCell) {
				continue;
			}
			Color colour = ca.getColour(neighbour);
			colours[i++] = colour;
			float difference = ColourCompare.getDifference(ca.getColour(cell),
					colour);
			if (difference > maxDifference) {
				maxDifference = difference;
			}
		}

		Color averageColour = ColourCompare.averageColour(colours);

		if (maxDifference < ca.getEpsilon()) {
			/*
			 * Sets pixel to the average colour of the surrounding pixels. Has a
			 * blurring effect.
			 */
			ca.setColour(cell, averageColour);
		}
	}
}
