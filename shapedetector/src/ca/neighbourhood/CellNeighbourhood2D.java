package ca.neighbourhood;

import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import exceptions.NullParameterException;

public abstract class CellNeighbourhood2D<V> implements Neighbourhood<V> {
	protected final Lattice<V> lattice;

	public CellNeighbourhood2D(final Lattice<V> lattice)
			throws NullParameterException {
		if (lattice == null) {
			throw new NullParameterException("lattice");
		}
		this.lattice = lattice;
	}

	/**
	 * Adds the cell to the neighbourhood if it is null (i.e. a padding cell).
	 * 
	 * @param neighbourhood
	 * @param cell
	 */
	protected void add(final List<Cell<V>> neighbourhood,
			final Cell<V> neighbour) {
		if (neighbour != null) {
			neighbourhood.add(neighbour);
		}
	}
}
