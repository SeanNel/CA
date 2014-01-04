package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

public class SDCircle extends SDEllipse {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 3.0E-4;

	private double width;

	public SDCircle() {
		super();
	}

	public SDCircle(SDEllipse shape) {
		super(shape);
		width = (shape.getLength() + shape.getWidth()) / 2.0;
	}

	protected void loadRelatedShapes() {
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
		// System.out.println(delta / area);
		if (delta / area < tolerance) {
			return new SDCircle(ellipse);
		} else {
			return ellipse;
		}
	}

	protected void getProperties() {
		Rectangle2D bounds = getBounds();
		width = (bounds.getHeight() - bounds.getWidth()) / 2.0;
	}

	protected String getDescription() {
		return "w=" + width;
	}
}