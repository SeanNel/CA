package ca.lattice;

import java.awt.Color;

import javax.swing.JLabel;

import ca.Cell;

import std.Picture;

public abstract class Lattice implements Iterable<Cell> {
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
	 */
	public Lattice() {
	}

	/**
	 * Creates a lattice corresponding to the target picture. Subclasses should
	 * extend this.
	 * 
	 * @param picture
	 */
	public void load(Picture picture) {
		pictureBefore = picture;
		pictureAfter = new Picture(picture); /* Creates copy of picture. */
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
	public Cell getCell(int x, int y) { // [] coordinates
		/* Method stub. */
		throw new RuntimeException();
	}

	/**
	 * Sets the target cell's colour.
	 * 
	 * @param cell
	 * @param colour
	 */
	public void setColour(Cell cell, Color colour) {
		/* Method stub. */
		throw new RuntimeException();
	}

	/**
	 * Gets the target cell's colour.
	 * 
	 * @param cell
	 * @param colour
	 */
	public Color getColour(Cell cell) {
		/* Method stub. */
		throw new RuntimeException();
	}

	/**
	 * Gets the output image as a JLabel for embedding in a JFrame.
	 * 
	 * @param cell
	 * @param colour
	 */
	public JLabel getJLabel() {
		return pictureAfter.getJLabel();
	}

	/**
	 * Saves a copy of pictureAfter in pictureBefore.
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
}
