package ca.shapedetector;

import ca.CACell;
import ca.CAModel;

public class ShapeDetectorCell extends CACell {

	public ShapeDetectorCell(int x, int y, CAModel caModel) {
		super(x, y, caModel);
	}

	public void process() {
		if (state == INACTIVE)
			return;

		// TODO: send out ants (threads) to find shape boundaries.
	}

}
