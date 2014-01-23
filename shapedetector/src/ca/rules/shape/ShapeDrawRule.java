package ca.rules.shape;

import graphics.SDPanel;
import graphics.SDPanelTheme;
import ca.shapedetector.ShapeList;
import ca.shapedetector.shapes.AbstractShape;
import ca.shapedetector.shapes.UnknownShape;

/**
 * Displays all the found shapes on the output image.
 */
public class ShapeDrawRule extends ShapeRule {
	protected final SDPanel sdPanel;

	public ShapeDrawRule(final ShapeList shapeList, final SDPanel sdPanel) {
		super(shapeList);
		this.sdPanel = sdPanel;
	}

	public synchronized void update(final AbstractShape shape) {
		if (shape.getClass() != UnknownShape.class) {
			sdPanel.setTheme(SDPanelTheme.RECOGNIZED);
		} else {
			sdPanel.setTheme(SDPanelTheme.UNRECOGNIZED);
		}
		sdPanel.draw(shape);
		// Input.waitForSpace();
	}
}
