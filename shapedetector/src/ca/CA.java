package ca;

import exceptions.CAException;
import graphics.PictureFrame;
import graphics.PicturePanel;
import helpers.Stopwatch;

import java.util.List;

import std.Picture;
import ca.concurrency.ThreadServer;
import ca.lattice.Lattice2D;
import ca.rules.cell.CellRule;

/**
 * Cellular automaton for processing an image.
 * 
 * @author Sean
 */
public class CA {
	protected Lattice2D lattice;
	/** Processes to apply to each cell in sequence. */
	public List<CellRule> cellRules;
	/** Currently active cell rule. */
	protected CellRule currentCellRule;
	/** Signals that the CA should display its results in a window. */
	protected boolean visible;
	/** A frame for displaying the output image. */
	protected PictureFrame pictureFrame;

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            The difference threshold expressed as a fraction. Determines
	 *            how neighbourhood cells affect this cell's state. Low values
	 *            mean that small differences between cells are ignored.
	 * @param r
	 *            Search radius. Determines the size of the neighbourhood.
	 */
	public CA() {
		lattice = new Lattice2D();
		PicturePanel panel = new PicturePanel();
		pictureFrame = new PictureFrame(panel);
	}

	/**
	 * Sets picture to process and initializes cell lattice.
	 * 
	 * @param picture
	 *            Picture to process.
	 */
	public void setPicture(Picture picture) {
		lattice.load(picture);
		pictureFrame.setImage(lattice.getResult().getImage());
	}

	/**
	 * Sets the picture to process and does so by updating cells until they are
	 * all done (that is, until they all become inactive).
	 * 
	 * @param picture
	 *            Picture to process.
	 * @return Processed picture.
	 */
	public Picture apply(Picture picture) {
		try {
			System.out.println(this.getClass().getSimpleName() + " started.");
			Stopwatch stopwatch = new Stopwatch();

			setPicture(picture);
			pictureFrame.setVisible(visible);

			stopwatch.print("Loading complete, elapsed time: ");

			for (CellRule rule : cellRules) {
				rule.start();
				boolean active = true;
				// int passes = 0;
				while (active) {
					// stopwatch.start();
					ThreadServer<Cell> threadServer = new ThreadServer<Cell>(rule, lattice, 8);
					active = threadServer.run();
					lattice.complete();
					// passes++;
					// if (active || passes > 0) {
					// System.out.println(" pass #" + passes +
					// ", elapsed time: "
					// + stopwatch.time() + " ms");
					// }
				}
				rule.end();
			}
		} catch (CAException e) {
			handleException(e);
		}

		return lattice.getResult();
	}

	protected void handleException(CAException e) {
		e.printStackTrace();
		System.exit(0);
	}

	/**
	 * Gets the output image.
	 */
	public Picture getResult() {
		return lattice.getResult();
	}

	public PicturePanel getPicturePanel() {
		return pictureFrame.getPicturePanel();
	}

	public Lattice2D getLattice() {
		return lattice;
	}
}