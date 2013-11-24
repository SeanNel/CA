package ca.cornerfinder;

import java.util.ArrayList;

import ca.CAModel;

public class CACornerFinder extends CAModel {
	ArrayList<CornerFinderCell> corners;

	public CACornerFinder(float epsilon, int r) {
		super(epsilon, r);
		corners = new ArrayList<CornerFinderCell>();
	}
	
	protected void loadCells() {
		// Instead of checking whether operations are within bounds all the
		// time, add a border of dead cells around the image. Use getCell (not
		// cells[x][y]) to get cell corresponding to the pixel (x,y).

		cells = new CornerFinderCell[pictureBefore.width() + 2 * r][pictureBefore.height()
				+ 2 * r];

		// Instantiate all the cells, excluding the dead border cells (saves a
		// bit of memory).
		for (int x = 0; x < pictureBefore.width(); x++) {
			for (int y = 0; y < pictureBefore.height(); y++) {
				cells[x + r][y + r] = new CornerFinderCell(x, y, this);
			}
		}
	}

	public ArrayList<CornerFinderCell> getCorners() {
		// If no corners have been found, most likely we have not updated yet.
		if (corners.size() == 0) {
			update();
		}
		return corners;
	}
}
