package ca.rules.cell;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import exceptions.CAException;

/* A cell rule that does nothing at all. */
public class DummyRule<V> extends CellRule<V> {

	public DummyRule(final Lattice<V> lattice, final Neighbourhood<V> neighbourhoodModel) throws CAException {
		super(lattice, neighbourhoodModel);
	}

	@Override
	public void update(final Cell<V> cell) {
		/* Does nothing but demonstrate the overhead of a cell rule. */
	}
}
