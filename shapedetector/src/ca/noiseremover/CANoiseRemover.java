package ca.noiseremover;

import graphics.ColourCompare;

import java.awt.Color;

import ca.CACell;
import ca.CA;

/**
 * Removes noise from an image, in particular, by smoothing out small isolated
 * areas of high contrast.
 * <p>
 * A cell changes to the average colour of its neighbourhood if it is not at a
 * point of high contrast.
 * 
 * @author Sean
 */
public class CANoiseRemover extends CA {
	public CANoiseRemover(float epsilon, int r) {
		super(epsilon, r);
	}

	public void updateCell(CACell cell) {
		super.updateCell(cell);
		CACell[] neighbourhood = cell.getNeighbourhood();
		Color[] colours = new Color[neighbourhoodSize];
		float maxDifference = 0f;

		for (int i = 0; i < neighbourhoodSize; i++) {
			CACell neighbour = neighbourhood[i];
			if (neighbour == paddingCell) {
				break;
			}
			Color colour = getColour(neighbour);
			colours[i] = colour;
			float difference = ColourCompare.getDifference(getColour(cell),
					colour);
			if (difference > maxDifference) {
				maxDifference = difference;
			}
		}

		Color averageColour = ColourCompare.averageColour(colours);

		if (maxDifference < epsilon) {
			// Set pixel to the average colour of the surrounding pixels. Has a
			// blurring effect.
			setColour(cell, averageColour);
		}

		cell.setState(CACell.INACTIVE);
	}

}
