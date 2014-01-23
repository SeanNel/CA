package ca.rules.cell;

import helpers.Stopwatch;
import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.rules.Rule;
import exceptions.CAException;
import exceptions.NullParameterException;

public abstract class CellRule<V> implements Rule<Cell<V>> {
	protected final Lattice<V> lattice;
	protected final Neighbourhood<V> neighbourhoodModel;
	protected final Stopwatch stopwatch;

	/**
	 * Constructor.
	 * 
	 * @param ca
	 * @throws CAException
	 */
	public CellRule(final Lattice<V> lattice,
			final Neighbourhood<V> neighbourhoodModel) throws CAException {
		if (lattice == null) {
			throw new NullParameterException("lattice");
		} else if (neighbourhoodModel == null) {
			throw new NullParameterException("neighbourhoodModel");
		}

		this.lattice = lattice;
		this.neighbourhoodModel = neighbourhoodModel;
		stopwatch = new Stopwatch();
	}

	@Override
	public void prepare() {
		stopwatch.start();
	}

	@Override
	public void complete() {
		System.out.println(toString() + ", elapsed time: " + stopwatch.time()
				+ " ms");
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
