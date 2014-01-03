package ca.rules.cacell;

import ca.CACell;
import ca.shapedetector.CAShapeDetector;

/* A cell rule that does nothing at all. */
public class CADummyRule extends CACellRule {
	protected CAShapeDetector ca;

	public CADummyRule(CAShapeDetector ca) {
		super(ca);
		this.ca = ca;
	}

	public void update(CACell cell) {
		/* Does nothing but demonstrate the overhead of a cell rule */
	}
}
