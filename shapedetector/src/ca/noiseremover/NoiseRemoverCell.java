package ca.noiseremover;

import java.awt.Color;

import graphics.ColourCompare;
import ca.CACell;
import ca.CAModel;

/**
 * This cell changes to the average colour of its neighbourhood if it is not at
 * a point of high contrast.
 * 
 * @author Sean
 */
public class NoiseRemoverCell extends CACell {

	public NoiseRemoverCell(int x, int y, CAModel caModel) {
		super(x, y, caModel);
	}

	public void process() {
		Color neighbourhood = getNeighbourhood();
		float maxDifference = 0f;

		for (int i = 0; i < neighbourhoodSize; i++) {
			Color colour = neighbours[i].getColour();
			float difference = ColourCompare.getDifference(getColour(), colour);
			if (difference > maxDifference) {
				maxDifference = difference;
			}
		}

		if (maxDifference < caModel.getEpsilon()) {
			// Set pixel to the average colour of the surrounding pixels. Has a
			// blurring effect.
			caModel.setPixel(x, y, neighbourhood);
		}

		setState(INACTIVE);
	}

}
