package ca.rules.cell;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import exceptions.CAException;

/**
 * Although other rules may gather neighbours themselves, this enables us to do
 * so expressly in a separate rule.
 * 
 * @author Sean
 */
public class GatherNeighboursRule<V> extends CellRule<V> {

	public GatherNeighboursRule(final Lattice<V> lattice,
			final Neighbourhood<V> neighbourhoodModel) throws CAException {
		super(lattice, neighbourhoodModel);
	}

	@Override
	public void update(final Cell<V> cell) throws CAException {
		cell.setNeighbourhood(neighbourhoodModel.gatherNeighbours(cell));
	}
}
