package ca.lattice;

import ca.Cell;
import exceptions.CAException;

/**
 * A lattice of cells.
 * 
 * @author Sean
 * 
 * @param <V>
 */
public interface Lattice<V> extends Iterable<Cell<V>> {
	/**
	 * Gets the cell at the specified coordinates.
	 * <p>
	 * Returns the paddingCell when coordinates are out of bounds.
	 * 
	 * @return Cell at the specified position.
	 * @throws CAException
	 *             if the parameters do not match the lattice dimensions.
	 */
	public Cell<V> get(final int... x) throws CAException;

	/**
	 * Gets the state of the specified cell.
	 * 
	 * @param cell
	 */
	public V getState(final Cell<V> cell);

	/**
	 * Sets the state of the specified cell.
	 * 
	 * @param cell
	 * @param value
	 */
	public void setState(final Cell<V> cell, final V value);

	/**
	 * Executes when a rule has finished working on the lattice.
	 */
	public void complete();

}
