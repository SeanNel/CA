package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

import ca.shapedetector.path.SDPath;
import exceptions.MethodNotImplementedException;

public class Ellipse extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.1;

	private double length;
	private double width;

	public Ellipse() {
		super();
	}

	public Ellipse(SDPath path) {
		super(path);
		getProperties();
	}

	public Ellipse(SDShape shape) {
		super(shape);
	}

	protected void loadRelatedShapes() {
//		relatedShapes.add(new SDCircle(picture));
	}

	protected SDShape identify(SDShape shape) throws MethodNotImplementedException {
		double match = compare(shape);

		if (1.0 - match < tolerance) {
			Ellipse ellipse = new Ellipse(shape);
			shape = Circle.identify(ellipse);
			return shape;
		} else {
			return null;
		}
	}

	protected void getProperties() {
		Rectangle2D rectangle = getBounds();

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
