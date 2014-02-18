package shapes;

import java.awt.geom.Rectangle2D;

import org.apache.commons.math3.util.FastMath;

import path.SDPath;



public class Circle extends Ellipse {
	protected final static double TOLERANCE = 0.06; // Polygon.TOLERANCE;

	Circle() {
		super();
	}

	public Circle(final SDPath path) {
		super();
	}

	public Circle(final AbstractShape shape) {
		super(shape);
	}

	/* Creates an ellipse that approximates the shape. */
	// protected Circle getMask(AbstractShape shape) {
	// SDPath path = getEllipse(shape);
	// return new Circle(path);
	// }

	public AbstractShape identify(final Ellipse ellipse) {
		AbstractShape shape = ellipse;
		Rectangle2D bounds = shape.getPath().getBounds();
		double w = bounds.getWidth();
		double h = bounds.getHeight();
		double d = (w - h) / Math.max(w, h);
		if (FastMath.abs(d) < TOLERANCE) {
			shape = new Circle(shape);
		}
		return shape;
	}

}
