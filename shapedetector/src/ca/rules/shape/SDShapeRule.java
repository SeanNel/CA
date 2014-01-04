package ca.rules.shape;

import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.shapes.SDShape;

/* TODO extend CACellRule */
public abstract class SDShapeRule {
	protected CAShapeDetector ca;

	/**
	 * Constructor.
	 * 
	 * @param ca
	 */
	public SDShapeRule(CAShapeDetector ca) {
		this.ca = ca;
	}

	public void update(SDShape shape) {
		/* Method stub. */
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
