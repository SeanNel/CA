package ca.neighbourhood;

import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.lattice.CellLattice2D;
import exceptions.NullParameterException;

public abstract class CellNeighbourhood2D implements Neighbourhood {
	protected final Lattice<Cell> lattice;

	public CellNeighbourhood2D(Lattice<Cell> lattice) throws NullParameterException {
		if (lattice == null) {
			throw new NullParameterException("lattice");
		}
		this.lattice = lattice;
	}

	/**
	 * Adds the cell to the neighbourhood if it is null or a paddingCell.
	 * 
	 * @param neighbourhood
	 * @param cell
	 */
	protected void add(List<Cell> neighbourhood, Cell neighbour) {
		if (neighbour != null && neighbour != CellLattice2D.paddingCell) {
			neighbourhood.add(neighbour);
		}
	}
}
