package ca.rules.shape;

import graphics.SDPanel;
import ca.shapedetector.ShapeList;
import ca.shapedetector.shapes.SDShape;
import ca.shapedetector.shapes.UnknownShape;

/**
 * Displays all the found shapes on the output image.
 */
public class ShapeDrawRule extends ShapeRule {
	protected SDPanel sdPanel;

	public ShapeDrawRule(ShapeList shapeList, SDPanel sdPanel) {
		super(shapeList);
		this.sdPanel = sdPanel;
	}

	public void update(SDShape shape) {
		/* using instanceof does not seem to work here. */
		if (shape.getClass() != UnknownShape.class) {
			sdPanel.setTheme(SDPanel.RECOGNIZED_THEME);
		} else {
			sdPanel.setTheme(SDPanel.UNRECOGNIZED_THEME);
		}
		sdPanel.draw(shape);
		// Input.waitForSpace();
	}
}
