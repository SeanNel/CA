package ca;

import graphics.ColourCompare;

import java.awt.Color;

/**
 * The operational unit of a cellular automaton.
 * 
 * @author Sean
 */
public class CACell {
	/**
	 * Colour that cells turn to when they become inactive, that is the
	 * background colour of the output image.
	 */
	public final static Color QUIESCENT_COLOUR = new Color(255, 255, 255);

	/*
	 * Enumerate possible states. I chose integers instead of a boolean state in
	 * case we need to add more states.
	 */
	/**
	 * Signals that cell is due to be processed next time process() is called.
	 * The default state.
	 */
	protected final static int ACTIVE = 0;
	/** Signals that cell has been processed and will not change any further. */
	protected final static int INACTIVE = 1;
	/**
	 * Denotes whether cell is ACTIVE or INACTIVE.
	 * <p>
	 * Note that this is distinct from the state of the cell as determined by
	 * its colour and the state of its neighbourhood.
	 */
	protected int state = ACTIVE;

	/** X-coordinate of the cell corresponding to the pixel in the source image. */
	protected int x;
	/** Y-coordinate of the cell corresponding to the pixel in the source image. */
	protected int y;

	/**
	 * An array of CACells in this cell's neighbourhood, specifically those
	 * cells within a certain distance from this cell.
	 */
	protected CACell[] neighbours;
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

	/** A reference to the parent CAModel. */
	protected CAModel caModel;

	/** A dead padding cell. */
	public final static CACell paddingCell = new CACell();

	/**
	 * Constructor useful for making singletons. Used by CAThreadServer and for
	 * creating the paddingCell.
	 */
	public CACell() {
		state = INACTIVE;
	}

	/**
	 * Creates a cell corresponding to the pixel (x,y) in the picture given to
	 * the CAModel.
	 * 
	 * @param x
	 * @param y
	 * @param caModel
	 */
	public CACell(int x, int y, CAModel caModel) {
		this.x = x;
		this.y = y;
		this.caModel = caModel;

		switch (neighbourhoodModel) {
		case MOORE_NEIGHBOURHOOD:
			neighbourhoodSize = 4 * caModel.getRadius() * caModel.getRadius();
			break;
		case VANNEUMANN_NEIGHBOURHOOD:
			neighbourhoodSize = (int) Math.ceil(Math.PI * caModel.getRadius()
					* caModel.getRadius());
			break;
		}
	}

	/**
	 * Initializes cell by gathering its neighbourhood according to the chosen
	 * model.
	 */
	protected void init() {
		switch (neighbourhoodModel) {
		case MOORE_NEIGHBOURHOOD:
			meetNeighboursMoore();
			break;
		case VANNEUMANN_NEIGHBOURHOOD:
			meetNeighboursVanNeumann();
			break;
		}
	}

	/**
	 * Seeks all neighbouring cells within the square 2r*2r centered on (x,y),
	 * that is its Moore neighbourhood.
	 */
	protected void meetNeighboursMoore() {
		neighbours = new CACell[neighbourhoodSize];
		int n = 0;

		int r = caModel.getRadius();
		for (int i = x - r; i <= x + r; i++) {
			for (int j = y - r; j <= y + r; j++) {
				CACell cell = caModel.getCell(i, j);
				neighbours[n] = cell;
			}
		}
		// System.out.println("neighbourhoodSize: " + neighbourhoodSize
		// + ", neighbours.length: " + neighbours.length);
	}

	/**
	 * Seeks all neighbouring cells within the given radius.
	 * <p>
	 * Begin by assuming all cells are in the square 2r*2r centered on (x,y).
	 * Then exclude the cells that are not inside the circle.
	 * <p>
	 * This method may give slightly better memory performance than the Moore
	 * neighbourhood.
	 * <p>
	 * Another way to find these cells may be to iterate row for row and adjust
	 * the y coordinate as a function of x.
	 */
	protected void meetNeighboursVanNeumann() {
		neighbours = new CACell[neighbourhoodSize];
		int n = 0;

		int r = caModel.getRadius();
		for (int i = x - r; i <= x + r; i++) {
			for (int j = y - r; j <= y + r; j++) {
				if (((i - x) * (i - x)) + ((j - y) * (j - y)) <= r * r) {
					CACell cell = caModel.getCell(i, j);
					neighbours[n] = cell;
				}
			}
		}
	}

	/**
	 * Determines the average state of all the cells in this cell's
	 * neighbourhood, specifically their average colour.
	 * 
	 * @return The average colour of this cell's neighbourhood.
	 */
	protected Color getNeighbourhood() {
		Color[] colours = new Color[neighbourhoodSize];
		for (int i = 0; i < neighbourhoodSize; i++) {
			colours[i] = neighbours[i].getColour();
		}
		return ColourCompare.averageColour(colours);
	}

	/**
	 * Initialize cell if it hasn't done so already and update cell.
	 */
	public void update() {
		if (neighbours == null) {
			init();
		}
		applyRule();
	}

	/**
	 * Defines the cell 'rule'. Child classes should override this.
	 */
	public void applyRule() {
	}

	/**
	 * Set the state of the cell.
	 * 
	 * @param state
	 *            Either ACTIVE or INACTIVE.
	 */
	public void setState(int state) {
		this.state = state;
		if (state == ACTIVE) {
			caModel.activate();
		}
	}

	/**
	 * Get the cell's colour.
	 * 
	 * @return The colour of the pixel at (x,y) in the original image.
	 */
	public Color getColour() {
		if (this == paddingCell) {
			return QUIESCENT_COLOUR;
		} else {
			return caModel.getPixel(x, y);
		}
	}

	/**
	 * Set the cell's colour in the output image.
	 */
	public void setColour(Color colour) {
		caModel.setPixel(x, y, colour);
	}

	/**
	 * States whether cell is due to update or not.
	 * 
	 * @return True when cell is ACTIVE.
	 */
	public boolean isActive() {
		return state == ACTIVE;
	}

	public String toString() {
		return "(CACell) x=" + x + ", y=" + y;
	}

	/**
	 * Get cell's x-coordinate that corresponds to a pixel in the source image.
	 * 
	 * @return Cell's x-coordinate.
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get cell's y-coordinate that corresponds to a pixel in the source image.
	 * 
	 * @return Cell's y-coordinate.
	 */
	public int getY() {
		return y;
	}
}