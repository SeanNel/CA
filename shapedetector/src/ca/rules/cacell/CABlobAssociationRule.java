package ca.rules.cacell;

import ca.CACell;
import ca.shapedetector.CABlob;
import ca.shapedetector.CAShapeDetector;

/**
 * Creates a CABlob object for each cell. 
 */
public class CABlobAssociationRule extends CACellRule {
	protected CAShapeDetector ca;

	public CABlobAssociationRule(CAShapeDetector ca) {
		super(ca);
		this.ca = ca;
	}

	public void update(CACell cell) {
		CABlob blob = new CABlob(cell);
		ca.setBlob(cell, blob);
		ca.addBlob(blob);
	}
}
