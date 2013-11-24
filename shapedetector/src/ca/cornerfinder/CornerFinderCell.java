package ca.cornerfinder;

import java.awt.Color;
import ca.CACell;
import ca.CAModel;

public class CornerFinderCell extends CACell {
	// Rays are lines radiating from the cell to search along.
	protected static final double rays[] = { 0f, Math.PI / 2, Math.PI,
			3 * Math.PI / 2 };
	// minN defines the minimum number of filled rays for the cell to
	// be an edge, maxN is the maximum.
	protected static final int minN = 2;
	protected static final int maxN = 2;

	protected double cornerAngle;

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

		boolean qRays[] = new boolean[rays.length];

		for (int i = 0; i < rays.length; i++) {
			qRays[i] = testRay(rays[i]);
		}

		int n = countQuiescentRays(qRays);
		if (n >= minN && n <= maxN) {
			// TODO: determine if this is a corner and find its angle
			// for (int i = 0; i < rays.length - 1; i++) {
			// if (qRays[i] && qRays[i + 1]) {
			//
			// }
			// }
		}

		state = INACTIVE; // Nothing more for the cell to do.
	}

	protected int countQuiescentRays(boolean[] array) {
		int n = 0;
		for (int i = 0; i < array.length; i++) {
			if (array[i])
				n++;
		}
		return n;
	}

	protected boolean testRay(double theta) {
		/*
		 * Returns true when a ray contains only quiescent cells. An alternative
		 * to using trig functions (which is expensive) is testing for specific
		 * theta values and hardcoding directions. This limits the number of
		 * available directions, however.
		 */
		for (int i = 0; i < caModel.getRadius(); i++) {
			int x = this.x + (int) Math.round(i * Math.cos(theta));
			int y = this.x + (int) Math.round(i * Math.sin(theta));
			Color colour = caModel.getPixel(x, y);
			if (!isQuiescent(colour)) {
				return false;
			}
		}
		return true;
	}

	protected boolean isQuiescent(Color colour) {
		/*
		 * Returns true when the colour is quiescent (same colour as an empty
		 * cell). Ideally we want to be able to do an object comparison, but
		 * that doesn't work because Picture doesn't save the colour reference
		 * to the pixel. It might be possible to rewrite it to do so, then we'll
		 * see a great improvement in performance here.
		 */

		// Object comparison:
		// return colour == CACell.QUIESCENT_COLOUR;

		// Array comparison:
		return colour.equals(QUIESCENT_COLOUR);
	}

}
