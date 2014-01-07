package ca.lattice;

import java.awt.Color;
import java.util.Iterator;

import ca.Cell;

import std.Picture;

public class Lattice2D extends Lattice {
	/** Two dimensional array of CACells. */
	protected Cell[][] lattice;
	/** Dead padding cell. */
	public final static Cell paddingCell = new Cell();

	private class CACellIterator implements Iterator<Cell> {
		protected Cell[][] lattice;
		protected int x = 0;
		protected int y = 0;

		protected int i = 0;
		protected int maxI;

		public CACellIterator(Cell[][] lattice) {
			this.lattice = lattice;
			if (lattice.length == 0 || lattice[0].length == 0) {
				throw new RuntimeException("Lattice size must be at least 1x1.");
			}
			maxI = lattice.length * lattice[0].length;
		}

		@Override
		public boolean hasNext() {
			return i < maxI;
		}

		@Override
		public Cell next() {
			Cell cell = lattice[x][y];
			i++;
			x++;
			if (x >= lattice.length) {
				y++;
				x = 0;
			}
			return cell;
		}

		@Override
		public void remove() {
			throw new RuntimeException("Remove not implemented.");
		}
	}

	/**
	 * Creates a lattice corresponding to the target picture.
	 * 
	 * @param picture
	 */
	public void load(Picture picture) {
		super.load(picture);

		int w = picture.width();
		int h = picture.height();
		lattice = new Cell[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				int[] coordinates = { x, y };
				lattice[x][y] = new Cell(coordinates);
			}
		}
	}

	/**
	 * Gets the cell corresponding to (x,y) in the source image.
	 * <p>
	 * Returns the paddingCell when coordinates are out of bounds.
	 * 
	 * @param x
	 *            Cell's x-coordinate.
	 * @param y
	 *            Cell's y-coordinate.
	 * @return Cell at the specified position.
	 */
	public Cell getCell(int x, int y) {
		if (x >= 0 && y >= 0 && x < lattice.length && y < lattice[0].length) {
			return lattice[x][y];
		} else {
			return paddingCell;
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
	public void setColour(Cell cell, Color colour) {
		int[] coordinates = cell.getCoordinates();
		pictureAfter.set(coordinates[0], coordinates[1], colour);

		/* This method supports transparency. */
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
	public Color getColour(Cell cell) {
		int[] coordinates = cell.getCoordinates();
		return pictureBefore.get(coordinates[0], coordinates[1]);
	}

	/**
	 * Gets the lattice width.
	 * 
	 * @return Lattice width.
	 */
	public int getWidth() {
		return lattice.length;
	}

	/**
	 * Gets the lattice height.
	 * 
	 * @return Lattice height.
	 */
	public int getHeight() {
		return lattice[0].length;
	}

	@Override
	public Iterator<Cell> iterator() {
		return new CACellIterator(lattice);
	}
}
