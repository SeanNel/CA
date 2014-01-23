package ca.rules.shape;

import java.awt.geom.Rectangle2D;

import graphics.SDPanel;
import helpers.Input;
import ca.shapedetector.ShapeList;
import ca.shapedetector.shapes.AbstractShape;

/**
 * Displays all the found shapes on the screen, in turn.
 */
public class ShapeDisplayRule extends ShapeRule {
	final SDPanel panel;

	public ShapeDisplayRule(final ShapeList shapeList, final SDPanel panel) {
		super(shapeList);
		this.panel = panel;
	}

	public void prepare() {
		panel.setVisible(true);
	}

	public synchronized void update(final AbstractShape shape) {
		Rectangle2D bounds = shape.getPath().getBounds();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
		panel.display(shape);
		Input.waitForSpace();
	}
}
