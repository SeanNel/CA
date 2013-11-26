package ca.shapedetector;

import graphics.Shape;

import java.util.ArrayList;
import std.Picture;
import ca.CAModel;

public class CAShapeDetector extends CAModel {
	ArrayList<Shape> detectedShapes;

	public CAShapeDetector(float epsilon, int r) {
		super(epsilon, r);
		detectedShapes = new ArrayList<Shape>();
	}

	public void setPicture(Picture picture) {
		detectedShapes = new ArrayList<Shape>();
		super.setPicture(picture);
	}

	protected void loadCells() {
		// Instead of checking whether operations are within bounds all the
		// time, add a border of dead cells around the image. Use getCell (not
		// cells[x][y]) to get cell corresponding to the pixel (x,y).

		cells = new ShapeDetectorCell[pictureBefore.width() + 2 * r][pictureBefore
				.height() + 2 * r];

		// Instantiate all the cells, excluding the dead border cells (saves a
		// bit of memory).
		for (int x = 0; x < pictureBefore.width(); x++) {
			for (int y = 0; y < pictureBefore.height(); y++) {
				cells[x + r][y + r] = new ShapeDetectorCell(x, y, this);
			}
		}
	}

	public ArrayList<Shape> detectShapes() {
		if (detectedShapes.size() == 0) {
			// If no shapes have been found, we probably need to update.
			update();
		}
		return detectedShapes;
	}

	public boolean update() {
		// Returns true if there are active cells remaining.

		// TODO: send out threads from cells (determined to potentially be
		// inside shapes, which we could verify using maps) and add detected
		// shapes to the shapes array.

		return super.update();
	}
}
