package ca.shapedetector.shapes;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import ca.shapedetector.Distribution;
import ca.shapedetector.ShapeDetector;
import ca.shapedetector.path.SDPath;
import graphics.SDPanel;

public class Quadrilateral extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.3;

	private double length;
	private double width;

	public Quadrilateral() {
		super();
	}

	public Quadrilateral(SDPath path) {
		super(path);
		getProperties();
		comparisonType = Distribution.RADIAL_DISTANCE_DISTRIBUTION;
	}

	public Quadrilateral(SDShape shape) {
		super(shape);
		comparisonType = Distribution.RADIAL_DISTANCE_DISTRIBUTION;
	}

	protected void loadRelatedShapes() {
		// relatedShapes.add(new SDRectangle(picture));
	}

	protected SDShape identify(SDShape shape) {
		/* For debugging */
		if (ShapeDetector.debug) {
			displayUnidentifiedShape(shape);
		}

		Quadrilateral identity = getIdentity(shape);
		if (identity == null) {
			return null;
		}

		/* For debugging */
		if (ShapeDetector.debug) {
			displayIdentityShape(shape, identity);
		}

		double match = identity.compare(shape);
		/* Input.waitforSpace() */

		if (1.0 - match < tolerance) {
			Quadrilateral quad = new Quadrilateral(shape);
			shape = Rectangle.identify(quad);
			return shape;
		} else {
			return null;
		}
	}

	protected void displayUnidentifiedShape(SDShape shape) {
//		graphics.ShapeFrame.setTheme(SDPanel.SIMPLE_THEME);
		graphics.ShapeFrame.setTheme(SDPanel.DEFAULT_THEME);
		graphics.ShapeFrame.reset(shape);
		graphics.ShapeFrame.display(shape);
	}

	protected void displayIdentityShape(SDShape shape, SDShape identity) {
		graphics.ShapeFrame.setTheme(SDPanel.IDENTITY_THEME);
		double[] cursor = graphics.ShapeFrame.getDrawCursor();
		double[] centre1 = shape.getCentre();
		double[] centre2 = identity.getCentre();
		double x = centre2[0] - centre1[0] + cursor[0];
		double y = centre2[1] - centre1[1] + cursor[1];
		graphics.ShapeFrame.moveDrawCursor(x, y);
		graphics.ShapeFrame.display(identity);
		// graphics.IdentityFrame.display(identity);
	}

	protected Quadrilateral getIdentity(SDShape shape) {
		/* Creates identity shape and compares the two. */
		Shape identityShape = Distribution.getPolygon(shape, 4);
		if (identityShape == null) {
			return null;
		}
		SDPath path = new SDPath(identityShape);
		Quadrilateral identity = new Quadrilateral(path);
		return identity;
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
