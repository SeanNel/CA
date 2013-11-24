package ca;

import graphics.Filter;

import java.awt.Color;

import std.Picture;
import std.StdDraw;

public class CAModel {
	protected CACell[][] cells;
	protected Picture pictureBefore;
	protected Picture pictureAfter;
	// Epsilon is the difference threshold. See graphics.ColourCompare
	protected float epsilon;
	protected int r; // Search radius around each cell.

	public CAModel(float epsilon, int r) {
		// Might want to set epsilon dynamically according to the colour range
		// in the image.
		this.epsilon = epsilon;
		this.r = r;
	}

	public void setPicture(Picture picture) {
		this.pictureBefore = picture;
		this.pictureAfter = new Picture(picture); // Create copy of picture.
		loadCells();
		activateCells();
	}

	protected void loadCells() {
		// Instead of checking whether operations are within bounds all the
		// time, add a border of dead cells around the image. Hopefully this
		// increases performance even if slightly. Memory use is
		// negligible since they are not initialized. Use getCell (not
		// cells[x][y]) to get cell corresponding to the pixel (x,y).

		cells = new CACell[pictureBefore.width() + 2 * r][pictureBefore.height() + 2
				* r];

		// First instantiate all the cells (excluding dead border cells).
		for (int x = 0; x < pictureBefore.width(); x++) {
			for (int y = 0; y < pictureBefore.height(); y++) {
				cells[x + r][y + r] = new CACell(x, y, this);
			}
		}
	}

	protected void activateCells() {
		// ... Now we can cross-reference the instantiated cells and activate
		// them.
		for (int x = 0; x < pictureBefore.width(); x++) {
			for (int y = 0; y < pictureBefore.height(); y++) {
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
		for (int x = 0; x < pictureBefore.width(); x++) {
			for (int y = 0; y < pictureBefore.height(); y++) {
				CACell cell = cells[x + r][y + r];
				if (cell.isActive()) {
					busy = true;
					cell.update();
					cell.process();
				}
			}
		}
		
		pictureBefore = new Picture(pictureAfter);
		return busy;
	}

	public void draw() {
		StdDraw.picture(0.5, 0.5, pictureAfter.getImage());
	}

	public CACell getCell(int x, int y) {
		return cells[x + r][y + r];
	}

	public Color getPixel(int x, int y) {
		return pictureBefore.get(x, y);
	}

	public void setPixel(int x, int y, Color colour) {
		pictureAfter.set(x, y, colour);
	}

	public int getRadius() {
		return r;
	}

	public void finish() {
		// Public because as yet, the CA never truly finishes so we choose an
		// arbitrary time and force it to finish.
		// Monochrome result because the border colours don't have much meaning.
		pictureAfter = Filter.monochrome(pictureAfter);
		draw();
	}

	public float getEpsilon() {
		return epsilon;
	}
}