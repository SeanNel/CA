package ca.rules.shape;

import graphics.SDPanel;
import helpers.Input;
import ca.shapedetector.ShapeList;
import ca.shapedetector.shapes.SDShape;

/**
 * Displays all the found shapes on the screen, in turn.
 */
public class ShapeDisplayRule extends ShapeRule {
	SDPanel sdPanel;

	public ShapeDisplayRule(ShapeList shapeList, SDPanel sdPanel) {
		super(shapeList);
		this.sdPanel = sdPanel;
	}

	public void start() {
		sdPanel.setVisible(true);
	}

	public void update(SDShape shape) {
		sdPanel.display(shape);
		Input.waitForSpace();
	}
}
