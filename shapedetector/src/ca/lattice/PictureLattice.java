package ca.lattice;

import helpers.NDArrayList;

import java.awt.Color;
import java.util.Iterator;

import std.Picture;

import ca.CACell;
import ca.Cell;
import exceptions.CAException;

public class PictureLattice implements Lattice<Color> {
	/** Two dimensional array of cells. */
	protected NDArrayList<Cell<Color>> lattice;
	/**
	 * Picture first given to the CA to process or in the event that the CA did
	 * not finish after its first pass, this is the output of the previous pass.
	 * <p>
	 * No changes are made to this picture.
	 */
	protected Picture pictureBefore;
	/**
	 * Starts off as a copy of the source image, but is subject to change as
	 * cells update.
	 */
	protected Picture pictureAfter;

	/**
	 * Constructor.
	 * 
	 * @param picture
	 * @throws CAException
	 */
	public PictureLattice(final Picture picture) throws CAException {
		pictureBefore = picture;
		pictureAfter = new Picture(picture);

		int w = picture.width();
		int h = picture.height();

		lattice = new NDArrayList<Cell<Color>>(w, h);
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int[] coordinates = { x, y };
				lattice.set(new CACell<Color>(this, coordinates), coordinates);
			}
		}
	}

	/**
	 * Gets the cell corresponding to (x,y) in the source image.
	 * <p>
	 * Returns null when coordinates are out of bounds.
	 * 
	 * @param x
	 *            Cell's x-coordinate.
	 * @param y
	 *            Cell's y-coordinate.
	 * @return Cell at the specified position.
	 * @throws CAException
	 */
	@Override
	public Cell<Color> get(final int... x) throws CAException {
		if (x.length != 2) {
			throw new CAException("Wrong number of dimensions");
		}
		int[] dimensions = lattice.getDimensions();
		if (x[0] >= 0 && x[1] >= 0 && x[0] < dimensions[0]
				&& x[1] < dimensions[1]) {
			return lattice.get(x);
		} else {
			return null;
		}
	}

	/**
	 * Sets the colour of the pixel corresponding to the cell.
	 * 
	 * @param cell
	 *            The cell to set the colour of.
	 * @param colour
	 *            The colour to set the cell to.
	 */
	public void setState(final Cell<Color> cell, final Color colour) {
		int[] coordinates = cell.getCoordinates();
		pictureAfter.set(coordinates[0], coordinates[1], colour);

		/* This method supports transparency (but is slower). */
		// Graphics graphics = pictureAfter.getImage().createGraphics();
		// graphics.setColor(colour);
		// graphics.fillRect(coordinates[0], coordinates[1], 1, 1);
	}

	/**
	 * Gets the colour of the pixel corresponding to the cell.
	 * 
	 * @param cell
	 *            The cell to get the colour of.
	 * @return Colour of the pixel at specified position.
	 */
	public Color getState(final Cell<Color> cell) {
		int[] coordinates = cell.getCoordinates();
		return pictureBefore.get(coordinates[0], coordinates[1]);
	}

	/**
	 * Gets the lattice width.
	 * 
	 * @return Lattice width.
	 */
	public int getWidth() {
		return lattice.getDimensions()[0];
	}

	/**
	 * Gets the lattice height.
	 * 
	 * @return Lattice height.
	 */
	public int getHeight() {
		return lattice.getDimensions()[1];
	}

	/**
	 * Saves a copy of pictureAfter in pictureBefore. Faster than creating a new
	 * PictureLattice with the output of the last one.
	 */
	public void complete() {
		pictureBefore = new Picture(pictureAfter);
	}

	/**
	 * Gets pictureAfter.
	 * 
	 * @return
	 */
	public Picture getResult() {
		return pictureAfter;
	}

	@Override
	public Iterator<Cell<Color>> iterator() {
		return lattice.iterator();
	}
}
