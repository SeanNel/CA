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
public class GatherNeighboursRule extends CellRule {

	public GatherNeighboursRule(Lattice lattice,
			Neighbourhood neighbourhoodModel) throws CAException {
		super(lattice, neighbourhoodModel);
	}

	public void update(Cell cell) {
		cell.setNeighbourhood(neighbourhoodModel.gatherNeighbours(cell));
	}
}
