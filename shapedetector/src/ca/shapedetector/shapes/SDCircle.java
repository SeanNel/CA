package ca.shapedetector.shapes;

import java.awt.Graphics2D;
import java.awt.Rectangle;

import ca.shapedetector.CAProtoShape;

public class SDCircle extends SDEllipse {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 3.0E-4;

	private double width;

	public SDCircle() {
	}

	public SDCircle(SDPath path, Graphics2D graphics) {
		super(path, graphics);
	}

	public SDCircle(SDEllipse shape) {
		super();
		path = shape.path;
		Rectangle bounds = shape.path.getBounds();
		width = (bounds.getHeight() + bounds.getWidth()) / 2.0;
	}

	protected SDShape identify(CAProtoShape protoShape) {
		return null;
	}

	/**
	 * Returns a circle if detected, otherwise returns the ellipse given as
	 * parameter.
	 * 
	 * @param shape
	 * @return
	 */
	protected static SDShape identify(SDEllipse ellipse) {
		double delta = Math.abs(ellipse.getLength() - ellipse.getWidth());
		double area = ellipse.getLength() * ellipse.getWidth();
//		 System.out.println(delta / area);
		if (delta / area < tolerance) {
			return new SDCircle(ellipse);
		} else {
			return ellipse;
		}
	}

	protected void getProperties() {
		/* Ensures that the shape is placed upright. */
		SDPath path = new SDPath(this.path);
		path.rotate((Math.PI / 4.0) - path.getOrientation());

		Rectangle bounds = path.getBounds();
		width = (bounds.getHeight() - bounds.getWidth()) / 2.0;

	}

	protected String getDescription() {
		return "w=" + width;
	}
}