package ca.rules.cell;

import helpers.Stopwatch;
import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.rules.Rule;
import exceptions.CAException;
import exceptions.NullParameterException;

public abstract class CellRule implements Rule<Cell> {
	protected final Lattice<Cell> lattice;
	protected final Neighbourhood neighbourhoodModel;
	protected final Stopwatch stopwatch;

	/**
	 * Constructor.
	 * 
	 * @param ca
	 * @throws CAException
	 */
	public CellRule(Lattice<Cell> lattice, Neighbourhood neighbourhoodModel)
			throws CAException {
		if (lattice == null) {
			throw new NullParameterException("lattice");
		} else if (neighbourhoodModel == null) {
			throw new NullParameterException("neighbourhoodModel");
		}

		this.lattice = lattice;
		this.neighbourhoodModel = neighbourhoodModel;
		stopwatch = new Stopwatch();
	}

	public void start() {
		stopwatch.start();
	}

	// public void update(Cell cell) {
	// /* Method stub. */
	// /*
	// * This bit should be added when subclasses extend this method, when
	// * needed.
	// */
	// if (cell.getState() != Cell.ACTIVE) {
	// return;
	// }
	// }

	public void end() {
		System.out.println(toString() + ", elapsed time: " + stopwatch.time()
				+ " ms");
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
