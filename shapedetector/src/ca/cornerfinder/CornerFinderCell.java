package ca.cornerfinder;

import ca.CACell;
import ca.CAModel;

public class CornerFinderCell extends CACell {
	public static final int NO_CORNER = 0;
	public static final int TOP_LEFT = 1;
	public static final int TOP_RIGHT = 2;
	public static final int BOTTOM_RIGHT = 3;
	public static final int BOTTOM_LEFT = 4;

	protected int cornerState;

	public CornerFinderCell(int x, int y, CAModel caModel) {
		super(x, y, caModel);
	}

	public void process() {
		/*
		 * The goal here is to identify corners, so that these corners can later
		 * be connected by graph vertices to identify shapes. If for example,
		 * the cells above and to the left are empty, this is probably a
		 * top-left corner. Since graphics are quantized to pixels, even circles
		 * will have corners defined in this way, the number of corners will
		 * just be much greater.
		 */

		if (state == INACTIVE)
			return;

		// Look to the left:
		boolean leftEmpty = true;
		for (int i = 0; i < caModel.getRadius(); i++) {
			if (caModel.getPixel(x - i, y) != CACell.QUIESCENT_COLOUR) {
				leftEmpty = false;
				break;
			}
		}

		// Look to the right:
		boolean rightEmpty = true;
		for (int i = 0; i < caModel.getRadius(); i++) {
			if (caModel.getPixel(x + i, y) != CACell.QUIESCENT_COLOUR) {
				rightEmpty = false;
				break;
			}
		}

		// Now, if both the left and right sides were not empty, this cannot be
		// an edge.
		if (leftEmpty || rightEmpty) {

			// Look above:
			boolean aboveEmpty = true;
			for (int i = 0; i < caModel.getRadius(); i++) {
				if (caModel.getPixel(x, y + i) != CACell.QUIESCENT_COLOUR) {
					aboveEmpty = false;
					break;
				}
			}

			// Look below:
			boolean belowEmpty = true;
			for (int i = 0; i < caModel.getRadius(); i++) {
				if (caModel.getPixel(x, y - i) != CACell.QUIESCENT_COLOUR) {
					belowEmpty = false;
					break;
				}
			}

			if (aboveEmpty && leftEmpty) {
				cornerState = TOP_LEFT;
			} else if (aboveEmpty && rightEmpty) {
				cornerState = TOP_RIGHT;
			} else if (belowEmpty && rightEmpty) {
				cornerState = BOTTOM_RIGHT;
			} else if (belowEmpty && leftEmpty) {
				cornerState = BOTTOM_LEFT;
			}

		}

		state = INACTIVE; // Nothing more for the cell to do.
	}

}
