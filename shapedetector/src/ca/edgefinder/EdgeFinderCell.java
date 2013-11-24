package ca.edgefinder;

import graphics.ColourCompare;
import ca.CACell;
import ca.CAModel;

public class EdgeFinderCell extends CACell {

	public EdgeFinderCell(int x, int y, CAModel caModel) {
		super(x, y, caModel);
	}

	public void process() {
		if (state == INACTIVE)
			return;

		if (1f - ColourCompare.getMatch(getColour(), getNeighbourhood()) < caModel
				.getEpsilon()) {
			disactivate();
		}
	}

}
