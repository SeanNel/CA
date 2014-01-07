package graphics;

public class IdentityFrame extends ShapeFrame {
	private static final long serialVersionUID = 1L;

	protected static final ShapeFrame shapeFrame = ShapeFrame.frame;
	public static final IdentityFrame frame = new IdentityFrame(new SDPanel());

	public IdentityFrame(SDPanel panel) {
		super(panel);
	}

	// protected void draw() {
	// super.draw();
	//
	// int x = shapeFrame.getX();
	// int y = shapeFrame.getY() + shapeFrame.getHeight() + 20;
	// setLocation(x, y);
	// }
}
