package ca;

import graphics.Filter;

import java.awt.Color;

import std.Picture;
import std.StdDraw;

public class CAModel {
	protected CACell[][] cells;
	protected Picture picture;
	// Epsilon is the difference threshold. See graphics.ColourCompare
	protected float epsilon;
	protected int r; // Search radius around each cell.

	public CAModel(float epsilon, int r) {
		// Might want to set epsilon dynamically according to the colour range
		// in
		// the image.
		this.epsilon = epsilon;
		this.r = r;
	}

	public void setPicture(Picture picture) {
		this.picture = picture;
		loadCells();
		activateCells();
	}

	protected void loadCells() {
		// Instead of checking whether operations are within bounds all the
		// time, add a border of dead cells around the image. Hopefully this
		// increases performance even if slightly. Memory use is
		// negligible since they are not initialized. Use getCell (not
		// cells[x][y]) to get cell corresponding to the pixel (x,y).

		cells = new CACell[picture.width() + 2 * r][picture.height() + 2 * r];

		// First instantiate all the cells (excluding dead border cells).
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				cells[x + r][y + r] = new CACell(x, y, this);
			}
		}
	}

	protected void activateCells() {
		// ... Now we can cross-reference the instantiated cells and activate
		// them.
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				CACell cell = cells[x + r][y + r];
				cell.activate();
				cell.meetNeighbours();
			}
		}
	}

	public boolean update() {
		// Returns true if there are active cells remaining. Currently there are
		// always some cells that remain active forever.

		boolean busy = false;
		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				CACell cell = cells[x + r][y + r];
				if (cell.isActive()) {
					busy = true;
					cell.update();
				}
			}
		}
		if (!busy) {
			// Algorithm has finished.
			return busy;
		}

		for (int x = 0; x < picture.width(); x++) {
			for (int y = 0; y < picture.height(); y++) {
				cells[x + r][y + r].process();
			}
		}

		return busy;
	}

	public void draw() {
		StdDraw.picture(0.5, 0.5, picture.getImage());
	}

	public CACell getCell(int x, int y) {
		return cells[x + r][y + r];
	}

	public Color getPixel(int x, int y) {
		return picture.get(x, y);
	}

	public void setPixel(int x, int y, Color colour) {
		picture.set(x, y, colour);
	}

	public int getRadius() {
		return r;
	}

	public void finish() {
		// Public because as yet, the CA never truly finishes so we choose an
		// arbitrary time and force it to finish.
		// Monochrome result because the border colours don't have much meaning.
		picture = Filter.monochrome(picture);
		draw();
	}

	public float getEpsilon() {
		return epsilon;
	}
}