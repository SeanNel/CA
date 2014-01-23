package ca;

import helpers.Output;

import java.util.List;

import ca.lattice.Lattice;

/**
 * The operational unit of a cellular automaton.
 * 
 * @author Sean
 */
public class CACell<V> implements Cell<V> {
	protected final Lattice<V> lattice;
	// protected V state;
	/**
	 * An array of CACells in this cell's neighbourhood, specifically those
	 * cells within a certain distance from this cell.
	 */
	protected List<Cell<V>> neighbourhood;
	/** The cell's position coordinates. */
	protected final int[] coordinates;

	/**
	 * Constructor.
	 * 
	 * @param coordinates
	 * @param state
	 *            The initial state of this cell.
	 * @param neighbourhood
	 */
	// public CACell(final Lattice<V> lattice, final V state,
	// final List<Cell<V>> neighbourhood, final int... coordinates) {
	// this.lattice = lattice;
	// this.coordinates = coordinates;
	// this.neighbourhood = neighbourhood;
	// setState(state);
	// }

	/**
	 * Constructor. Cell starts active, with a null state and an empty
	 * neighbourhood.
	 */
	public CACell(final Lattice<V> lattice, final int... coordinates) {
		this.lattice = lattice;
		this.coordinates = coordinates;
	}

	/**
	 * Sets this cell's state.
	 * 
	 * @state
	 */
	// @Override
	public void setState(final V state) {
		// this.state = state;
		lattice.setState(this, state);
	}

	/**
	 * Gets this cell's state.
	 * 
	 * @return
	 */
	// @Override
	public V getState() {
		// return state;
		return lattice.getState(this);
	}

	/**
	 * Gets the specified cell's neighbourhood, specifically those cells within
	 * a certain distance from this cell.
	 * 
	 * @param cell
	 *            The cell to get the neighbourhood of.
	 * @return A list of CACell's from this cell's neighbourhood.
	 */
	@Override
	public List<Cell<V>> getNeighbourhood() {
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
	@Override
	public void setNeighbourhood(final List<Cell<V>> neighbourhood) {
		this.neighbourhood = neighbourhood;
	}

	/**
	 * Gets this cell's coordinates.
	 * 
	 * @return This cell's coordinates.
	 */
	@Override
	public int[] getCoordinates() {
		return coordinates;
	}

	public String toString() {
		// return "[x=" + coordinates[0] + ", y=" + coordinates[1] + "]";
		return "(CACell) [" + Output.toString(coordinates) + ", state="
				+ getState() + "]";
	}
}