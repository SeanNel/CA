package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

import std.Picture;

import ca.shapedetector.path.SDPath;

public class SDRectangle extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.12;

	protected static SDPath identity;

	private double length;
	private double width;

	public SDRectangle(Picture picture) {
		super(picture);
		loadIdentity();
	}

	public SDRectangle(SDPath path, Picture picture) {
		super(path, picture);
		getProperties();
	}

	protected void loadRelatedShapes() {
		relatedShapes.add(new SDSquare(picture));
	}

	public void loadIdentity() {
		if (identity == null) {
			/*
			 * The size of the identity shape is arbitrary, but large values
			 * give better results.
			 */
			Rectangle2D rectangle = new Rectangle2D.Double(0, 0, 300, 300);
			identity = new SDPath(rectangle);
			// identity.calculateOrientation();
			// identity.rotate(identity.getOrientation());
		}
	}

	protected SDShape identify(SDPath path) {
		/* Ensures that the shape is placed upright. */
		SDPath rotatedPath = new SDPath(path);
		rotatedPath.rotate(rotatedPath.getOrientation());

		double match = identity.getDifference(rotatedPath);

		if (1.0 - match < tolerance) {
			SDRectangle rectangle = new SDRectangle(path, picture);
			SDShape shape = SDSquare.identify(rectangle);
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

	// public static void main(String[] args) {
	// Picture picture = new Picture(400, 400);
	// picture.setOriginLowerLeft();
	// Graphics2D graphics = picture.getImage().createGraphics();
	// graphics.setColor(Color.white);
	// graphics.fillRect(0, 0, 400,400);
	// // Rectangle2D rectangle = new Rectangle2D.Double(50, 50, 100, 100);
	// Ellipse2D rectangle = new Ellipse2D.Double(-50, -50, 200, 100);
	// SDPath p = new SDPath(rectangle);
	// // p.rotate(-Math.PI / 8.0);
	// p.calculateOrientation();
	// // p.displayHighlight(picture);
	// new SDRectangle(picture).identify(p);
	// }
}
