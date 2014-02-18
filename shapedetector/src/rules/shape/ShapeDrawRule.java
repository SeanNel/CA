package rules.shape;

import rules.AbstractRule;
import shapes.AbstractShape;
import shapes.UnknownShape;
import graphics.SDPanel;
import graphics.SDPanelTheme;

/**
 * Displays all the found shapes on the output image.
 */
public class ShapeDrawRule extends AbstractRule<AbstractShape> {
	protected final SDPanel sdPanel;

	public ShapeDrawRule(final SDPanel sdPanel) {
		super();
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
