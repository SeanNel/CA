package ca;

import java.util.List;

/**
 * The operational unit of a cellular automaton.
 * 
 * @author Sean
 */
public interface Cell<V> {
	/**
	 * Gets this cell's coordinates.
	 * 
	 * @return This cell's coordinates.
	 */
	public int[] getCoordinates();

	/**
	 * Gets the specified cell's neighbourhood, specifically those cells within
	 * a certain distance from this cell.
	 * 
	 * @return A list of CACell's from this cell's neighbourhood.
	 */
	public List<Cell<V>> getNeighbourhood();

	/**
	 * Sets the specified cell's neighbourhood.
	 * 
	 * @param neighbourhood
	 *            The neighbourhood to set to.
	 */
	public void setNeighbourhood(final List<Cell<V>> neighbourhood);

	/**
	 * Gets this cell's state.
	 * 
	 * @return
	 */
	 public V getState();

	/**
	 * Sets this cell's state.
	 * 
	 * @state
	 */
	 public void setState(final V state);
}