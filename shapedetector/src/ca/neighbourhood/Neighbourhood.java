package ca.neighbourhood;

import java.util.List;

import ca.Cell;

public interface Neighbourhood {

	/**
	 * Caches the neighbouring cells of the specified cell.
	 * 
	 * @param cell
	 *            The cell to initialize.
	 */
	public List<Cell> gatherNeighbours(Cell cell);

}
