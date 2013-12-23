package ca.rules.cacell;

import ca.CACell;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;

/**
 * Creates a CAProtoShape object for each cell. 
 */
public class CAProtoShapeAssociationRule extends CACellRule {
	protected CAShapeDetector ca;

	public CAProtoShapeAssociationRule(CAShapeDetector ca) {
		super(ca);
		this.ca = ca;
	}

	public void update(CACell cell) {
		CAProtoShape protoShape = new CAProtoShape(cell);
		ca.setProtoShape(cell, protoShape);
		ca.addProtoShape(protoShape);
	}
}
