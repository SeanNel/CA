package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

import std.Picture;

public class SDSquare extends SDRectangle {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 4.0E-4;

	private double width;

	public SDSquare(Picture picture) {
		super(picture);
	}

	protected void loadRelatedShapes() {
	}

	public SDSquare(SDRectangle shape) {
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
	protected static SDShape identify(SDRectangle rectangle) {
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
		Rectangle2D bounds = getPath().getBounds();
		width = (bounds.getHeight() - bounds.getWidth()) / 2.0;

	}

	protected String getDescription() {
		return "w=" + width;
	}
}
