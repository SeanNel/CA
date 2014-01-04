package ca.rules.shape;

import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.shapes.SDShape;

/**
 * Displays all the found shapes on the screen, in turn.
 */
public class SDShapeDisplayRule extends SDShapeRule {

	public SDShapeDisplayRule(CAShapeDetector ca) {
		super(ca);
	}

	public void update(SDShape shape) {
		shape.getPath().display(CAShapeDetector.shapeFrame);
		// Input.waitForSpace();
	}
}
