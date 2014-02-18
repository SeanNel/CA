package rules.shape;

import java.awt.geom.Rectangle2D;

import rules.AbstractRule;
import shapes.AbstractShape;

import graphics.SDPanel;

/**
 * Displays all the found shapes on the screen, in turn.
 */
public class ShapeDisplayRule extends AbstractRule<AbstractShape> {
	final SDPanel panel;

	public ShapeDisplayRule(final SDPanel panel) {
		super();
		this.panel = panel;
	}

	public void prepare() {
		panel.setVisible(true);
	}

	public synchronized void update(final AbstractShape shape) {
		Rectangle2D bounds = shape.getPath().getBounds();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
		panel.display(shape);
	}
}
