package graphics;

import java.awt.geom.Rectangle2D;

import ca.shapedetector.shapes.AbstractShape;

public class IdentityFrame extends ShapeFrame {
	private static final long serialVersionUID = 1L;

	protected static final ShapeFrame shapeFrame = ShapeFrame.frame;
	private static final SDPanel panel = new SDPanel();
	public static final IdentityFrame frame = new IdentityFrame(panel);

	public IdentityFrame(final SDPanel panel) {
		super(panel);
		setTitle("Identity shape");
	}

	public static void display(final AbstractShape shape) {
		int x = shapeFrame.getX();
		int y = shapeFrame.getY() + shapeFrame.getHeight() + 20;
		frame.setLocation(x, y);

		Rectangle2D bounds = shape.getPath().getBounds();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
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
