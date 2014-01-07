package ca.rules.cell;

import helpers.Stopwatch;
import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.rules.Rule;
import exceptions.CAException;
import exceptions.NullParameterException;

public abstract class CellRule implements Rule<Cell> {
	protected Lattice lattice;
	protected Neighbourhood neighbourhoodModel;
	protected final Stopwatch stopwatch = new Stopwatch();

	/**
	 * Constructor.
	 * 
	 * @param ca
	 * @throws CAException
	 */
	public CellRule(Lattice lattice, Neighbourhood neighbourhoodModel)
			throws CAException {
		if (lattice == null) {
			throw new NullParameterException("lattice");
		} else if (neighbourhoodModel == null) {
			throw new NullParameterException("neighbourhoodModel");
		}

		this.lattice = lattice;
		this.neighbourhoodModel = neighbourhoodModel;
	}

	public void start() {
		stopwatch.start();
	}

	public void update(Cell cell) {
		if (cell.getState() != Cell.ACTIVE) {
			return;
		}
		/* Method stub. */
	}

	public void end() {
		System.out.println(toString() + ", elapsed time: " + stopwatch.time()
				+ " ms");
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
