package ca.rules.cacell;

import ca.CA;
import ca.CACell;

public class CAGatherNeighboursRule extends CACellRule {

	public CAGatherNeighboursRule(CA ca) {
		super(ca);
	}

	public void update(CACell cell) {
		cell.setNeighbourhood(ca.gatherNeighbours(cell));
	}
}
