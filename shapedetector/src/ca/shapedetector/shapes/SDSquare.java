package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

import std.Picture;

import ca.shapedetector.CAProtoShape;

public class SDSquare extends SDRectangle {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 4.0E-4;

	private double width;

	public SDSquare(Picture picture) {
		super(picture);
	}

	public SDSquare(SDRectangle shape, Picture picture) {
		super(picture);
		path = shape.path;
		Rectangle2D bounds = shape.path.getBounds();
		width = (bounds.getHeight() + bounds.getWidth()) / 2.0;
	}
	
	protected void loadRelatedShapes() {
	}

	protected SDShape identify(CAProtoShape protoShape) {
		return null;
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
			return new SDSquare(rectangle, rectangle.picture);
		} else {
			return rectangle;
		}
	}

	protected void getProperties() {
		Rectangle2D bounds = path.getBounds();
		width = (bounds.getHeight() - bounds.getWidth()) / 2.0;

	}

	protected String getDescription() {
		return "w=" + width;
	}
}
