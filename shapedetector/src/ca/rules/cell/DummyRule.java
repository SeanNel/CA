package ca.rules.cell;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import exceptions.CAException;

/* A cell rule that does nothing at all. */
public class DummyRule extends CellRule {

	public DummyRule(Lattice lattice, Neighbourhood neighbourhoodModel) throws CAException {
		super(lattice, neighbourhoodModel);
	}

	public void update(Cell cell) {
		/* Does nothing but demonstrate the overhead of a cell rule. */
	}
}
