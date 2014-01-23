package ca.neighbourhood;

import java.util.List;

import ca.Cell;
import exceptions.CAException;

public interface Neighbourhood<V> {

	/**
	 * Caches the neighbouring cells of the specified cell.
	 * 
	 * @param cell
	 *            The cell to initialize.
	 * @throws CAException
	 */
	public List<Cell<V>> gatherNeighbours(final Cell<V> cell) throws CAException;

}
