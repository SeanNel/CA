package ca.noiseremover;

import ca.CACell;
import ca.CAModel;

/**
 * Removes noise from an image, in particular, by smoothing out small isolated
 * areas of high contrast.
 * 
 * @author Sean
 */
public class CANoiseRemover extends CAModel {
	public CANoiseRemover(float epsilon, int r) {
		super(epsilon, r);
	}

	protected CACell newCell(int x, int y, CAModel caModel) {
		return new NoiseRemoverCell(x, y, caModel);
	}

}
