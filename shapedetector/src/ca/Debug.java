package ca;

import graphics.SDPanelTheme;

import java.awt.geom.Rectangle2D;

import ca.shapedetector.shapes.AbstractShape;

public class Debug {

	/**
	 * When true, displays the shape being processed, distribution graphs and
	 * other debug info while running. Unpredictable output when running in
	 * parallel mode, so forces single-threaded processing.
	 */
	public static boolean debug = false;

	/**
	 * For debugging. Displays the active shape.
	 * 
	 * @param shape
	 */
	public static void displayActiveShape(AbstractShape shape) {
		// graphics.ShapeFrame.setTheme(SDPanel.SIMPLE);
		graphics.ShapeFrame.reset(shape);
		graphics.ShapeFrame.setTheme(SDPanelTheme.DEFAULT);
		graphics.ShapeFrame.display(shape);
	}

	/**
	 * For debugging. Displays the identity shape.
	 * 
	 * @param shape
	 */
	public static void displayMaskShape(AbstractShape shape, AbstractShape mask) {
		graphics.ShapeFrame.setTheme(SDPanelTheme.MASK);
		double[] cursor = graphics.ShapeFrame.getDrawCursor();

		Rectangle2D a = shape.getPath().getBounds();
		Rectangle2D b = mask.getPath().getBounds();

		double x = b.getCenterX() - a.getCenterX() + cursor[0];
		double y = b.getCenterY() - a.getCenterY() + cursor[1];

		graphics.ShapeFrame.moveDrawCursor(x, y);
		graphics.ShapeFrame.display(mask);
		// graphics.MaskFrame.display(identity);
	}
}
