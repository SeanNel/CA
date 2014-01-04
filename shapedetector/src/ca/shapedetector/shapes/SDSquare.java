package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

public class SDSquare extends SDQuadrilateral {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 4.0E-4;

	private double width;

	public SDSquare() {
		super();
	}

	protected void loadRelatedShapes() {
	}

	public SDSquare(SDQuadrilateral shape) {
		super(shape);
		width = (shape.getLength() + shape.getWidth()) / 2.0;
	}

	/**
	 * Returns a square if detected, otherwise returns the rectangle given as
	 * parameter.
	 * 
	 * @param shape
	 * @return
	 */
	protected static SDShape identify(SDQuadrilateral rectangle) {
		double delta = Math.abs(rectangle.getLength() - rectangle.getWidth());
		double area = rectangle.getLength() * rectangle.getWidth();
//		System.out.println(delta / area);
		if (delta / area < tolerance) {
			return new SDSquare(rectangle);
		} else {
			return rectangle;
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
