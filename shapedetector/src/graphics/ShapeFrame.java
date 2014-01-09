package graphics;

import ca.shapedetector.shapes.SDShape;

public class ShapeFrame extends PictureFrame {
	private static final long serialVersionUID = 1L;

	public static final SDPanel panel = new SDPanel();
	public static final ShapeFrame frame = new ShapeFrame(panel);

	public ShapeFrame(SDPanel panel) {
		super(panel);
		setTitle("Shape");
	}

	public static void display(SDShape shape) {
		panel.display(shape);
		frame.pack();
		frame.setVisible(true);
	}

	public static void setTheme(int theme) {
		panel.setTheme(theme);
	}

	public static void reset(SDShape shape) {
		double[] dimensions = shape.getDimensions();
		panel.reset((int) dimensions[0], (int) dimensions[1]);
	}

	public static void moveDrawCursor(double x, double y) {
		panel.moveDrawCursor(x, y);
	}

	public static double[] getDrawCursor() {
		return panel.getDrawCursor();
	}
}
