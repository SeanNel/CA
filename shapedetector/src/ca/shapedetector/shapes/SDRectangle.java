package ca.shapedetector.shapes;

import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;

public class SDRectangle extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.12;

	protected static SDPath identity;

	private double length;
	private double width;

	public SDRectangle() {
	}

	public SDRectangle(Graphics2D graphics) {
		super(graphics);
		loadIdentity();
	}

	public SDRectangle(SDPath path, Graphics2D graphics) {
		super(path, graphics);
		getProperties();
	}

	public void loadIdentity() {
		if (identity == null) {
			/*
			 * The size of the identity shape is arbitrary, but large values
			 * give better results.
			 */
			Rectangle2D rectangle = new Rectangle2D.Double(0, 0, 600, 600);
			identity = new SDPath(rectangle);
		}
	}

	protected SDShape identify(SDPath path) {
		/* Ensures that the shape is placed upright. */
		SDPath rotatedPath = new SDPath(path);
		rotatedPath.rotate((Math.PI / 4.0) - rotatedPath.getOrientation());

		double match = identity.getDifference(rotatedPath);

		if (1.0 - match < tolerance) {
			SDRectangle rectangle = new SDRectangle(path, graphics);
			SDShape shape = SDSquare.identify(rectangle);
			return shape;
		} else {
			return null;
		}
	}

	protected void getProperties() {
		Rectangle rectangle = path.getBounds();

		length = rectangle.getWidth();
		width = rectangle.getHeight();
	}

	protected String getDescription() {
		return "l=" + length + ", w=" + width;
	}

	public double getLength() {
		return length;
	}

	public double getWidth() {
		return width;
	}
}
