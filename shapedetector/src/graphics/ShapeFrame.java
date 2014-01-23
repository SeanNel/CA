package graphics;

import java.awt.geom.Rectangle2D;

import ca.shapedetector.shapes.AbstractShape;

public class ShapeFrame extends PictureFrame {
	private static final long serialVersionUID = 1L;

	public static final SDPanel panel = new SDPanel();
	public static final ShapeFrame frame = new ShapeFrame(panel);

	public ShapeFrame(final SDPanel panel) {
		super(panel);
		setTitle("Shape");
	}

	public static void display(final AbstractShape shape) {
		panel.display(shape);
		frame.pack();
		frame.setVisible(true);
	}

	public static void setTheme(final SDPanelTheme theme) {
		panel.setTheme(theme);
	}

	public static void reset(final AbstractShape shape) {
		Rectangle2D bounds = shape.getPath().getBounds();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
	}

	public static void moveDrawCursor(final double x, final double y) {
		panel.moveDrawCursor(x, y);
	}

	public static double[] getDrawCursor() {
		return panel.getDrawCursor();
	}
}
