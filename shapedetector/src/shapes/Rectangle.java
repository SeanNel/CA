package shapes;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

import path.SDPath;

import utils.AngleUtils;


public class Rectangle extends Quadrilateral {
	protected final static double TOLERANCE = 0.3d;

	protected final static Square square = new Square();

	Rectangle() {
		super();
	}

	public Rectangle(final SDPath path) {
		super(path);
	}

	public Rectangle(final AbstractShape shape) {
		super(shape);
	}

	/**
	 * Assumes that shape is a quadrilateral (i.e. has only four vertices).
	 * 
	 * @param shape
	 */
	public AbstractShape identify(final AbstractShape abstractShape) {
		AbstractShape shape = abstractShape;
		List<Point2D> vertices = shape.getPath().getVertices();
		/* A better method might be to compare the lengths of opposing sides */
		List<Double> gradients = new ArrayList<Double>(4);

		Point2D a = vertices.get(3);
		for (Point2D b : vertices) {
			double x = b.getX() - a.getX();
			double y = b.getY() - a.getY();
			double theta = AngleUtils.getAngle(x, y);
			theta = AngleUtils.representativeAngle(theta);
			gradients.add(theta);
			a = b;
		}

		double d1 = gradients.get(0) - gradients.get(2);
		double d2 = gradients.get(1) - gradients.get(3);

		if (FastMath.abs(d1) < TOLERANCE && FastMath.abs(d2) < TOLERANCE) {
			/* Opposite sides are parallel, so this is a parallelogram. */
			double gradient1 = (gradients.get(0) + gradients.get(2)) / 2d;
			double gradient2 = (gradients.get(1) + gradients.get(3)) / 2d;

			double d3 = gradient1 + gradient2;
			d3 = AngleUtils.representativeAngle(d3);
			d3 = FastMath.abs((FastMath.PI / 2d) - d3);
			if (d3 < TOLERANCE) {
				/* Adjacent sides are orthogonal, so this is a rectangle. */
				shape = new Rectangle(shape);
				// getMask
				shape = shape.identifySubclass();
			}
		}

		return shape;
	}

	@Override
	protected AbstractShape identifySubclass() {
		AbstractShape shape = square.identify(this);
		return shape;
	}

	// protected Rectangle getMask(AbstractShape shape) {
	// SDPath path = getPolygon(shape, 4);
	// return new Rectangle(path);
	// }
}
