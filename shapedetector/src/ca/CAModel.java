package ca;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;

import std.Picture;
import std.StdDraw;
import ca.concurrency.CAThreadServer;

/**
 * Cellular automaton for processing an image.
 * 
 * @author Sean
 */
public class CAModel {
	/**
	 * Two dimensional array of CACells. Does not exactly correspond to the
	 * pixels in the source image since extra cells are added around as padding.
	 */
	protected CACell[][] cells;
	/**
	 * Picture first given to this CAModel to process or in the event that the
	 * CAModel did not finish after its first pass, this is the output of the
	 * previous pass.
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
	 * The difference threshold expressed as a fraction. Determines how
	 * neighbourhood cells affect this cell's state. Low values mean that small
	 * differences between cells are ignored.
	 * 
	 * @see graphics.ColourCompare
	 */
	protected float epsilon;
	/** Search radius. Determines the size of the neighbourhood. */
	protected int r;
	/** Signals whether there are cells that are still due to update. */
	protected boolean active;
	/** Coordinates threads to update cells. */
	protected CAThreadServer threadServer;
	/** Useful for determining the performance of this program. */
	protected Stopwatch stopwatch;
	/** CACell class to use with this CAModel. */
	protected Class<CACell> cellClass = CACell.class;
	/** Number of times this CAModel has processed its cells. */
	protected int passes;

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
	public CAModel(float epsilon, int r) {
		/*
		 * Might instead want to set epsilon dynamically according to the colour
		 * range in the image.
		 */
		this.epsilon = epsilon;
		this.r = r;
		stopwatch = new Stopwatch();
	}

	/**
	 * Gets a new instance of the appropriate CACell subclass. Avoids having to
	 * write a loadCells() method for each subclass of CAModel.
	 * 
	 * @param x
	 *            Cell's x-coordinate
	 * @param y
	 *            Cell's y-coordinate
	 * @param caModel
	 *            A reference to this CAModel.
	 * @return A cell object which is a subclass of CACell.
	 */
	protected CACell newCell(int x, int y, CAModel caModel) {
		return new CACell(x, y, caModel);
	}

	/**
	 * Sets picture to process and initializes cells.
	 * 
	 * @param picture
	 *            Picture to process.
	 */
	public void setPicture(Picture picture) {
		stopwatch.start();

		pictureBefore = picture;
		pictureAfter = new Picture(picture); /* Create copy of picture. */
		try {
			loadCells();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}

		stopwatch.print(this.getClass().getSimpleName() + " loading time: ");
	}

	/** Gets the output image. */
	public Picture getPicture() {
		return pictureAfter;
	}

	/**
	 * Initializes cells.
	 * <p>
	 * Instead of checking whether operations are within bounds all the time,
	 * adds a border of dead cells around the image. Hopefully this increases
	 * performance even if slightly. Memory use is negligible.
	 * <p>
	 * Use getCell (not cells[x][y]) to get cell corresponding to the pixel
	 * (x,y).
	 * 
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 */
	protected void loadCells() throws InstantiationException,
			IllegalAccessException, NoSuchMethodException, SecurityException,
			IllegalArgumentException, InvocationTargetException {
		/*
		 * Adds dead padding cells around the image to avoid having to check
		 * boundary conditions all the time.
		 */
		cells = new CACell[pictureBefore.width() + r + r][pictureBefore
				.height() + r + r];

		for (int x = 0; x < cells.length; x++) {
			for (int y = 0; y < cells[0].length; y++) {
				// cells[x + r][y + r] = new CACell(x, y, this);
				if (x >= r && x < pictureBefore.width() + r && y >= r
						&& y < pictureBefore.height() + r) {
					cells[x][y] = newCell(x - r, y - r, this);
				} else {
					cells[x][y] = CACell.paddingCell;
				}
			}
		}
	}

	/**
	 * Set the picture to process and process it by updating cells until they
	 * are all done (that is, until they all become inactive).
	 * 
	 * @param picture
	 *            Picture to process.
	 * @return Processed picture.
	 */
	public Picture apply(Picture picture) {
		setPicture(picture);
		stopwatch.start();

		active = true;
		while (active) {
			process();
		}

		stopwatch.print(this.getClass().getSimpleName() + " running time: ");
		return pictureAfter;
	}

	/**
	 * Give cells to the thread server for them to process as a number of
	 * separate threads.
	 * <p>
	 * Redraws the picture on screen after each pass and sets pictureBefore to
	 * the updated image.
	 */
	public void process() {
		active = false;
		threadServer = new CAThreadServer();
		threadServer.start();
		for (int x = 0; x < pictureBefore.width(); x++) {
			for (int y = 0; y < pictureBefore.height(); y++) {
				threadServer.enqueue(getCell(x, y));
			}
		}
		threadServer.finish();
		synchronized (threadServer) {
			passes++;
			draw();
			pictureBefore = new Picture(pictureAfter);
		}
	}

	/** Displays the modified image on screen. */
	public void draw() {
		StdDraw.picture(0.5, 0.5, pictureAfter.getImage());
	}

	/**
	 * Gets the cell corresponding to (x,y) in the source image.
	 * <p>
	 * Use this method instead of cells[x][y] since that array does not
	 * correspond exactly to pixels from the image.
	 * 
	 * @param x
	 *            Cell's x-coordinate.
	 * @param y
	 *            Cell's y-coordinate.
	 * @return Cell at the specified position.
	 */
	public CACell getCell(int x, int y) {
		return cells[x + r][y + r];
	}

	/**
	 * Gets the colour of the pixel at (x,y) in the source image.
	 * 
	 * @param x
	 *            Pixel's x-coordinate.
	 * @param y
	 *            Pixel's y-coordinate.
	 * @return Colour of the pixel at specified position.
	 */
	public Color getPixel(int x, int y) {
		return pictureBefore.get(x, y);
	}

	/**
	 * Sets the colour of the pixel at (x,y) in the output image.
	 * 
	 * @param x
	 *            Pixel's x-coordinate.
	 * @param y
	 *            Pixel's y-coordinate.
	 */
	public void setPixel(int x, int y, Color colour) {
		pictureAfter.set(x, y, colour);
	}

	/**
	 * Gets the search radius.
	 * 
	 * @return The search radius (r). Determines the size of the neighbourhood.
	 */
	public int getRadius() {
		return r;
	}

	/**
	 * Gets the difference threshold.
	 * 
	 * @return The difference threshold (epsilon).
	 */
	public float getEpsilon() {
		return epsilon;
	}

	/**
	 * Gets the number of times this CAModel has processed its cells.
	 * 
	 * @return The number of passes.
	 */
	public int getPasses() {
		return passes;
	}

	/**
	 * Sets active to true. Causes this CAModel to update its active cells once
	 * done with its current pass.
	 * <p>
	 * Called from CACell.
	 */
	public void activate() {
		active = true;
	}

	/**
	 * Gets the cell object of the type specific to this CAModel.
	 * <p>
	 * Used in CACellThread. Subclasses should override this.
	 * 
	 * @param cell
	 *            Cell to fetch.
	 * @return Cell object of type that extends CACell.
	 */
	public CACell getCell(CACell cell) {
		return cell;
	}
}