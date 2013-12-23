package ca.rules.protoshape;

import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;

public abstract class CAProtoShapeRule {
	protected CAShapeDetector ca;

	/**
	 * Constructor.
	 * 
	 * @param ca
	 */
	public CAProtoShapeRule(CAShapeDetector ca) {
		this.ca = ca;
	}

	/**
	 * Applies the rule and updates the specified protoShape.
	 * 
	 * @param protoShape
	 */
	public void update(CAProtoShape protoShape) {
		/** Method stub. */
	}
	
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
