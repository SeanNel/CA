package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

import ca.shapedetector.path.SDPath;

public class SDEllipse extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.1;

	private double length;
	private double width;

	public SDEllipse() {
		super();
	}

	public SDEllipse(SDPath path) {
		super(path);
		getProperties();
	}

	public SDEllipse(SDShape shape) {
		super(shape);
	}

	protected void loadRelatedShapes() {
//		relatedShapes.add(new SDCircle(picture));
	}

	protected SDShape identify(SDShape shape) {
		double match = compare(shape);

		if (1.0 - match < tolerance) {
			SDEllipse ellipse = new SDEllipse(shape);
			shape = SDCircle.identify(ellipse);
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
