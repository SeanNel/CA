package ca.shapedetector.shapes;

import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import ca.shapedetector.path.SDPath;

import std.Picture;

public class SDEllipse extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.1;

	protected static final SDCircle circle = new SDCircle();
	protected static SDPath identity;

	private double length;
	private double width;

	public SDEllipse() {
	}

	public SDEllipse(Picture picture) {
		super(picture);
		loadIdentity();
	}

	public SDEllipse(SDPath path, Picture picture) {
		super(path, picture);
		getProperties();
	}

	public void loadIdentity() {
		if (identity == null) {
			/*
			 * The size of the identity shape is arbitrary, but large values
			 * give better results.
			 */
			Ellipse2D ellipse = new Ellipse2D.Double(0, 0, 100, 100);
			identity = new SDPath(ellipse);
			// identity.calculateOrientation();
			// identity.rotate((Math.PI / 4.0) - identity.getOrientation());
		}
	}

	protected SDShape identify(SDPath path) {
		/* Ensures that the shape is placed upright. */
		SDPath rotatedPath = new SDPath(path);
		rotatedPath.rotate((Math.PI / 4.0) - rotatedPath.getOrientation());

		double match = identity.getDifference(rotatedPath);

		if (1.0 - match < tolerance) {
			SDEllipse ellipse = new SDEllipse(path, picture);
			SDShape shape = SDCircle.identify(ellipse);
			return shape;
		} else {
			return null;
		}
	}

	protected void getProperties() {
		Rectangle2D rectangle = path.getBounds();

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
