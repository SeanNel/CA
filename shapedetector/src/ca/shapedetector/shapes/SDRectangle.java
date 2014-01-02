package ca.shapedetector.shapes;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import std.Picture;

import ca.shapedetector.path.SDPath;

public class SDRectangle extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.12;

	private double length;
	private double width;

	/**
	 * Identity constructor.
	 * 
	 * @param picture
	 */
	public SDRectangle(Picture picture) {
		super(picture);

		/*
		 * The size of the identity shape is arbitrary, but large values may
		 * give better results.
		 */
		Rectangle2D rectangle = new Rectangle2D.Double(0, 0, 100, 100);
		Path2D path = new Path2D.Double();
		path.append(rectangle, true);

		/*
		 * TODO: either flip the rectangle or get a reversed PathIterator. The
		 * rectangle is built clockwise but our SDShapes are built
		 * anti-clockwise.
		 */

		loadPath(new SDPath(rectangle));
		// identity.calculateOrientation();
		// identity.rotate(identity.getOrientation());
	}

	public SDRectangle(SDPath path, Picture picture) {
		super(path, picture);
		getProperties();
	}

	public SDRectangle(SDShape shape) {
		super(shape);
	}

	protected void loadRelatedShapes() {
		relatedShapes.add(new SDSquare(picture));
	}

	protected SDShape identify(SDShape shape) {
		double match = compare(shape);

		if (1.0 - match < tolerance) {
			SDRectangle rectangle = new SDRectangle(getPath(), picture);
			shape = SDSquare.identify(rectangle);
			return shape;
		} else {
			return null;
		}
	}

	protected void getProperties() {
		Rectangle2D rectangle = getPath().getBounds();

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
