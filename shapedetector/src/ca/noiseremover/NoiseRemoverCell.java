package ca.noiseremover;

import graphics.ColourCompare;
import ca.CACell;
import ca.CAModel;

public class NoiseRemoverCell extends CACell {

	public NoiseRemoverCell(int x, int y, CAModel caModel) {
		super(x, y, caModel);
	}

	public void process() {
		if (state == INACTIVE)
			return;

		if (1f - ColourCompare.getMatch(getColour(), neighbourhood) > caModel
				.getEpsilon()) {
			// Set pixel to the average colour of the surrounding pixels. Has a
			// blurring effect.
			caModel.setPixel(x, y, neighbourhood);
		}
	}

}
