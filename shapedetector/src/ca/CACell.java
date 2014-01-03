package ca;

import java.util.List;

/**
 * The operational unit of a cellular automaton.
 * 
 * @author Sean
 */
public class CACell {
	/*
	 * Enumerate possible states. I chose integers instead of a boolean state in
	 * case it becomes necessary to add more states.
	 */
	/**
	 * Signals that cell is due to be processed next time process() is called.
	 * The default state.
	 */
	public final static int ACTIVE = 1;
	/** Signals that cell has been processed and will not change any further. */
	public final static int INACTIVE = 0;
	/**
	 * Denotes whether cell is ACTIVE or INACTIVE.
	 * <p>
	 * Note that this is distinct from the state of the cell as determined by
	 * its colour and the state of its neighbourhood.
	 */
	protected int state;
	/**
	 * An array of CACells in this cell's neighbourhood, specifically those
	 * cells within a certain distance from this cell.
	 */
	protected List<CACell> neighbourhood;
	/** The cell's position coordinates. */
	protected final int[] coordinates;
	/* TODO: Move this to an array in shapedetector. */
	protected boolean validate;

	/**
	 * Constructor.
	 * 
	 * @param coordinates
	 * @param state
	 *            The initial state of this cell.
	 * @param neighbourhood
	 */
	public CACell(int[] coordinates, int state, List<CACell> neighbourhood) {
		this.coordinates = coordinates;
		this.state = state;
		this.neighbourhood = neighbourhood;
	}

	/**
	 * Constructor. Cell starts active with an empty neighbourhood.
	 */
	public CACell(int[] coordinates) {
		this.coordinates = coordinates;
		state = ACTIVE;
	}

	/**
	 * Singleton constructor.
	 */
	public CACell() {
		coordinates = null;
		state = INACTIVE;
	}

	/**
	 * Sets this cell's state.
	 * 
	 * @state Either ACTIVE or INACTIVE.
	 */
	public void setState(int state) {
		this.state = state;
	}

	/**
	 * Gets this cell's state.
	 * 
	 * @return This cell's state: either ACTIVE or INACTIVE.
	 */
	public int getState() {
		return state;
	}

	/**
	 * Gets the specified cell's neighbourhood, specifically those cells within
	 * a certain distance from this cell.
	 * 
	 * @param cell
	 *            The cell to get the neighbourhood of.
	 * @return A list of CACell's from this cell's neighbourhood.
	 */
	public List<CACell> getNeighbourhood() {
		return neighbourhood;
	}

	/**
	 * Sets the specified cell's neighbourhood.
	 * 
	 * @param cell
	 *            The cell to get the neighbourhood of.
	 * @param neighbourhood
	 *            The neighbourhood to set to.
	 */
	public void setNeighbourhood(List<CACell> neighbourhood) {
		this.neighbourhood = neighbourhood;
	}

	/**
	 * Gets this cell's coordinates.
	 * 
	 * @return This cell's coordinates.
	 */
	public int[] getCoordinates() {
		return coordinates;
	}

	public String toString() {
		return "(CACell) [x=" + coordinates[0] + ", y=" + coordinates[1]
				+ ", state=" + state + "]";
	}
}