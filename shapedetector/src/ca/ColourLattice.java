package ca;

import java.awt.Color;

import utils.Picture;

/**
 * A lattice of pixel colours.
 * 
 * @author Sean
 * 
 * @param <V>
 */
public class ColourLattice extends Lattice2D<Color> {
	/**
	 * Starts off as a copy of the source image, but is subject to change as
	 * cells update.
	 */
	protected final Picture picture;

	/**
	 * Constructor.
	 * 
	 * @param picture
	 */
	public ColourLattice(final Picture picture) throws Exception {
		super(Color.class, picture.width(), picture.height());
		/* It saves about 500ms if we don't make a copy of the picture. */
		this.picture = picture;
		// this.picture = new Picture(picture);

		int w = picture.width();
		int h = picture.height();

		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				Color colour = picture.get(x, y);
				statesBefore[x][y] = colour;
				statesAfter[x][y] = colour;
			}
		}
	}

	/**
	 * Sets the state of the cell along with the colour of the pixel
	 * corresponding to the cell.
	 * 
	 * @param cell
	 *            The cell to set the colour of.
	 * @param colour
	 *            The colour to set the cell to.
	 * @throws Exception
	 */
	@Override
	public void setState(final Cell cell, final Color state) throws Exception {
		super.setState(cell, state);
		setCellColour(cell, state);
	}

	// TODO: is sync really necessary here?
	// synchronized
	protected void setCellColour(final Cell cell, final Color state) {
		int[] x = cell.getCoordinates();
		picture.set(x[0], x[1], getColour(state));
	}

	protected Color getColour(final Color state) {
		return state;
	}

	/**
	 * Gets the output picture.
	 * 
	 * @return
	 */
	public Picture getPicture() {
		return picture;
	}
}
