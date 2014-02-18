package neighbourhood;

import java.util.Collection;

import ca.Cell;

public interface Neighbourhood {

	/**
	 * Gets the cells in the specified cell's neighbourhood from the lattice.
	 * 
	 * @param cell
	 * @throws Exception
	 */
	public Collection<Cell> neighbours(Cell cell)
			throws Exception;

	/**
	 * Clears all caches values to free memory.
	 */
	public void clear();
}
