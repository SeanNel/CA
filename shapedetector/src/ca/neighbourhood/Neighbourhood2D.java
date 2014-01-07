package ca.neighbourhood;

import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.lattice.Lattice2D;
import exceptions.NullParameterException;

public abstract class Neighbourhood2D implements Neighbourhood {
	protected Lattice lattice;

	public Neighbourhood2D(Lattice lattice) throws NullParameterException {
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
	protected void add(List<Cell> neighbourhood, Cell cell) {
		if (cell != null && cell != Lattice2D.paddingCell) {
			neighbourhood.add(cell);
		}
	}
}
