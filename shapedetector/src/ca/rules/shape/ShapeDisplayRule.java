package ca.rules.shape;

import graphics.SDPanel;
import helpers.Input;
import ca.shapedetector.ShapeList;
import ca.shapedetector.shapes.SDShape;

/**
 * Displays all the found shapes on the screen, in turn.
 */
public class ShapeDisplayRule extends ShapeRule {
	SDPanel panel;

	public ShapeDisplayRule(ShapeList shapeList, SDPanel panel) {
		super(shapeList);
		this.panel = panel;
	}

	public void start() {
		panel.setVisible(true);
	}

	public void update(SDShape shape) {
		double[] dimensions = shape.getDimensions();
		panel.reset((int) dimensions[0], (int) dimensions[1]);
		panel.display(shape);
		Input.waitForSpace();
	}
}
