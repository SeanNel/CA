package shapes;

import java.awt.geom.Point2D;
import java.util.List;

import org.apache.commons.math3.util.FastMath;

import path.SDPath;



public class Square extends Quadrilateral {
	protected final static double TOLERANCE = 0.1d;

	protected final static Square square = new Square();

	Square() {
		super();
	}

	public Square(final SDPath path) {
		super(path);
	}

	public Square(final AbstractShape shape) {
		super(shape);
	}

	public AbstractShape identify(final Quadrilateral shape) {
		List<Point2D> vertices = shape.getPath().getVertices();

		double length = shape.getPath().getPerimeter() / 4d;
		Point2D a = vertices.get(3);
		for (Point2D b : vertices) {
			double d = (length - a.distance(b)) / length;
			if (FastMath.abs(d) > TOLERANCE) {
				return shape;
			}
			a = b;
		}

		return new Square(shape);
	}

	// protected Rectangle getMask(final AbstractShape shape) {
	// SDPath path = getPolygon(shape, 4);
	// return new Rectangle(path);
	// }
}
