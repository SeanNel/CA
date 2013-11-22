package ca.noiseremover;

import ca.CAModel;

public class CANoiseRemover extends CAModel {

	public CANoiseRemover(float epsilon, int r) {
		super(epsilon, r);
	}
	
	protected void loadCells() {
		// Instead of checking whether operations are within bounds all the
		// time, add a border of dead cells around the image. Use getCell (not
		// cells[x][y]) to get cell corresponding to the pixel (x,y).

		cells = new NoiseRemoverCell[picture.width() + 2 * r][picture.height()
				+ 2 * r];

		// Instantiate all the cells, excluding the dead border cells (saves a
		// bit of memory).
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				cells[x + r][y + r] = new NoiseRemoverCell(x, y, this);
			}
		}
	}

}