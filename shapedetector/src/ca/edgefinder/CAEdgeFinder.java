package ca.edgefinder;

import ca.CAModel;

public class CAEdgeFinder extends CAModel {

	public CAEdgeFinder(float epsilon, int r) {
		super(epsilon, r);
	}

	protected void loadCells() {
		// Instead of checking whether operations are within bounds all the
		// time, add a border of dead cells around the image. Use getCell (not
		// cells[x][y]) to get cell corresponding to the pixel (x,y).

		cells = new EdgeFinderCell[pictureBefore.width() + 2 * r][pictureBefore.height()
				+ 2 * r];

		// Instantiate all the cells, excluding the dead border cells (saves a
		// bit of memory).
		for (int x = 0; x < pictureBefore.width(); x++) {
			for (int y = 0; y < pictureBefore.height(); y++) {
				cells[x + r][y + r] = new EdgeFinderCell(x, y, this);
			}
		}
	}

}
