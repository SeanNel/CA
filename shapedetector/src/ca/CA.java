package ca;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import std.Picture;
import std.StdDraw;
import ca.concurrency.CAThreadServer;
import ca.rules.cacell.CACellRule;

/**
 * Cellular automaton for processing an image.
 * 
 * @author Sean
 */
public class CA {
	/** Two dimensional array of CACells. */
	protected CACell[][] lattice;
	/** Processes to apply to each cell in sequence. */
	public List<CACellRule> cellRules;
	/** Currently active cell rule. */
	CACellRule currentCellRule;
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
	private float epsilon;
	/** Search radius. Determines the size of the neighbourhood. */
	protected int r;
	/** Signals whether there are cells that are still due to update. */
	protected boolean active;
	/** Coordinates threads to update cells. */
	protected CAThreadServer threadServer;
	/** Number of times this CAModel has processed its cells. */
	protected int passes;
	/** Dead padding cell. */
	public final static CACell paddingCell = new CACell();
	/* Stopwatches useful for determining the performance of this program. */
	/** Keeps time of how long it takes to complete a pass. */
	protected Stopwatch passStopwatch;
	/** Keeps time of how long it takes to complete a rule. */
	protected Stopwatch ruleStopwatch;

	/** Moore neighbourhood (a square). */
	public final static int MOORE_NEIGHBOURHOOD = 0;
	/** Van Neuman neighbourhood (a circle). */
	public final static int VANNEUMANN_NEIGHBOURHOOD = 1;
	/**
	 * The model used to represent the cell's neighbourhood, either
	 * MOORE_NEIGHBOURHOOD (a square) or VANNEUMANN_NEIGHBOURHOOD (a circle).
	 */
	protected int neighbourhoodModel = MOORE_NEIGHBOURHOOD;
	/** Stores the calculated size of the neighbourhood. */
	protected int neighbourhoodSize;
	/**
	 * Signals that the image should be image should be drawn after each update.
	 */
	protected boolean drawOnModelUpdate;
	/**
	 * Signals that the image should be image should be drawn after each update.
	 */
	protected boolean drawOnCellUpdate;

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
	public CA(float epsilon, int r) {
		/*
		 * Might instead want to set epsilon dynamically according to the colour
		 * range in the image.
		 */
		this.epsilon = epsilon;
		this.r = r;
		passStopwatch = new Stopwatch();
		ruleStopwatch = new Stopwatch();
	}

	/**
	 * Sets picture to process and initializes cell lattice.
	 * 
	 * @param picture
	 *            Picture to process.
	 */
	public void setPicture(Picture picture) {
		pictureBefore = picture;
		pictureAfter = new Picture(picture); /* Creates copy of picture. */
		loadLattice();
	}

	/**
	 * Gets the output image.
	 */
	public Picture getPicture() {
		return pictureAfter;
	}

	/**
	 * Initializes the lattice of cells.
	 */
	protected void loadLattice() {
		switch (neighbourhoodModel) {
		case MOORE_NEIGHBOURHOOD:
			neighbourhoodSize = 4 * r * r;
			break;
		case VANNEUMANN_NEIGHBOURHOOD:
			neighbourhoodSize = (int) Math.ceil(Math.PI * r * r);
			break;
		}

		lattice = new CACell[pictureBefore.width()][pictureBefore.height()];

		for (int x = 0; x < lattice.length; x++) {
			for (int y = 0; y < lattice[0].length; y++) {
				int[] coordinates = { x, y };
				lattice[x][y] = new CACell(coordinates);
			}
		}
	}

	/**
	 * Gathers all neighbouring cells within the square 3r*3r centered on (x,y),
	 * that is its Moore neighbourhood.
	 * 
	 * @param cell
	 *            Cell to find the neighbourhood of.
	 * @return Array of cells in the neighbourhood.
	 */
	protected List<CACell> gatherNeighboursMoore(CACell cell, int r) {
		List<CACell> neighbourhood = new ArrayList<CACell>(neighbourhoodSize);
		int[] coordinates = cell.getCoordinates();
		for (int i = coordinates[0] - r; i < coordinates[0] + r; i++) {
			for (int j = coordinates[1] - r; j < coordinates[1] + r; j++) {
				neighbourhood.add(getCell(i, j));
			}
		}
		return neighbourhood;
	}

	/**
	 * Gathers all neighbouring cells within the given radius.
	 * <p>
	 * Begin by assuming all cells are in the square 3r*3r centered on (x,y).
	 * Then exclude the cells that are not inside the circle.
	 * <p>
	 * This method may give slightly better memory performance than the Moore
	 * neighbourhood.
	 * <p>
	 * Another way to find these cells may be to iterate row for row and adjust
	 * the y coordinate as a function of x.
	 * 
	 * @param cell
	 *            Cell to find the neighbourhood of.
	 * @return Array of cells in the neighbourhood.
	 */
	protected List<CACell> gatherNeighboursVanNeumann(CACell cell, int r) {
		List<CACell> neighbourhood = new ArrayList<CACell>(neighbourhoodSize);
		int[] coordinates = cell.getCoordinates();
		for (int i = coordinates[0] - r; i < coordinates[0] + r; i++) {
			for (int j = coordinates[1] - r; j < coordinates[1] + r; j++) {
				if (((i - coordinates[0]) * (i - coordinates[0]))
						+ ((j - coordinates[1]) * (j - coordinates[1])) <= r
						* r) {
					neighbourhood.add(getCell(i, j));
				}
			}
		}
		return neighbourhood;
	}

	/**
	 * Sets the picture to process and processes it by updating cells until they
	 * are all done (that is, until they all become inactive).
	 * 
	 * @param picture
	 *            Picture to process.
	 * @return Processed picture.
	 */
	public Picture apply(Picture picture) {
		System.out.println(this.getClass().getSimpleName() + " started.");
		ruleStopwatch.start();

		setPicture(picture);
		if (drawOnModelUpdate || drawOnCellUpdate) {
			draw();
		}

		ruleStopwatch.print("Loading complete, elapsed time: ");
		ruleStopwatch.start();

		Iterator<CACellRule> ruleIterator = cellRules.iterator();
		if (ruleIterator.hasNext()) {
			currentCellRule = ruleIterator.next();
		} else {
			throw new RuntimeException("No cell rules defined...");
		}

		active = true;
		while (active) {
			passStopwatch.start();
			updateModel();

			passes++;
			pictureBefore = new Picture(pictureAfter);
			if (drawOnModelUpdate) {
				draw();
			}
			if (!active) {
				System.out.println(currentCellRule + ", elapsed time: "
						+ ruleStopwatch.time() + " ms");
				if (ruleIterator.hasNext()) {
					passes = 0;
					currentCellRule = ruleIterator.next();
					ruleStopwatch.start();
					active = true;
				}
			} else {
				System.out.println(" pass #" + passes + ", elapsed time: "
						+ passStopwatch.time() + " ms");
			}
		}
		return pictureAfter;
	}

	/**
	 * Hands cells to the thread server for them to process as a number of
	 * separate threads.
	 * <p>
	 * Redraws the picture on screen after each pass and sets pictureBefore to
	 * the updated image.
	 */
	protected void updateModel() {
		active = false;
		threadServer = new CAThreadServer(this);
		threadServer.start();
		for (int x = 0; x < lattice.length; x++) {
			for (int y = 0; y < lattice[0].length; y++) {
				threadServer.enqueue(lattice[x][y]);
			}
		}
		threadServer.finish();
		synchronized (threadServer) {
			/* Wait for all the threads to finish. */
		}
	}

	/**
	 * Applies subsequent changes to the CA that do not relate to individual
	 * cells. Subclasses should extend this.
	 */
	protected void postProcess() {
		/* Method stub */
	}

	/**
	 * Applies the current process to the cell.
	 * 
	 * @param cell
	 *            The cell to update.
	 */
	public void updateCell(CACell cell) {
		currentCellRule.update(cell);

		if (drawOnCellUpdate && cell.validate) {
			int[] coordinates = cell.getCoordinates();
			StdDraw.setPenColor(pictureAfter
					.get(coordinates[0], coordinates[1]));
			StdDraw.drawPixel(coordinates[0], coordinates[1]);
			cell.validate = false;
		}
	}

	/**
	 * Caches the neighbouring cells of the specified cell.
	 * 
	 * @param cell
	 *            The cell to initialize.
	 */
	public List<CACell> gatherNeighbours(CACell cell) {
		List<CACell> neighbourhood = null;
		switch (neighbourhoodModel) {
		case MOORE_NEIGHBOURHOOD:
			neighbourhood = gatherNeighboursMoore(cell, r);
			break;
		case VANNEUMANN_NEIGHBOURHOOD:
			neighbourhood = gatherNeighboursVanNeumann(cell, r);
			break;
		}
		return neighbourhood;
	}

	/**
	 * Displays the modified image on screen.
	 */
	public void draw() {
		StdDraw.picture(pictureAfter.width() / 2, pictureAfter.height() / 2,
				pictureAfter.getImage());
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
	public CACell getCell(int x, int y) {
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
	public void setColour(CACell cell, Color colour) {
		int[] coordinates = cell.getCoordinates();
		pictureAfter.set(coordinates[0], coordinates[1], colour);
		cell.validate = true;
	}

	/**
	 * Gets the colour of the pixel corresponding to the cell.
	 * 
	 * @param cell
	 *            The cell to get the colour of.
	 * @return Colour of the pixel at specified position.
	 */
	public Color getColour(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		return pictureBefore.get(coordinates[0], coordinates[1]);
	}

	/**
	 * Gets the number of times this CA has processed its cells.
	 * 
	 * @return The number of passes.
	 */
	public int getPasses() {
		return passes;
	}

	/**
	 * @return The epsilon value.
	 */
	public float getEpsilon() {
		return epsilon;
	}
}