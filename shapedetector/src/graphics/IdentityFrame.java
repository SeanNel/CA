package graphics;

import ca.shapedetector.shapes.SDShape;

public class IdentityFrame extends ShapeFrame {
	private static final long serialVersionUID = 1L;

	protected static final ShapeFrame shapeFrame = ShapeFrame.frame;
	private static final SDPanel panel = new SDPanel();
	public static final IdentityFrame frame = new IdentityFrame(panel);

	public IdentityFrame(SDPanel panel) {
		super(panel);
		setTitle("Identity shape");
	}

	public static void display(SDShape shape) {
		int x = shapeFrame.getX();
		int y = shapeFrame.getY() + shapeFrame.getHeight() + 20;
		frame.setLocation(x, y);

		double[] dimensions = shape.getDimensions();
		panel.reset((int) dimensions[0], (int) dimensions[1]);
		panel.display(shape);
		frame.pack();
		frame.setVisible(true);
	}

	// protected void draw() {
	// super.draw();
	//
	// int x = shapeFrame.getX();
	// int y = shapeFrame.getY() + shapeFrame.getHeight() + 20;
	// setLocation(x, y);
	// }
}
