package ca.shapedetector.shapes;

import java.awt.geom.Point2D;
import java.util.List;

import ca.shapedetector.path.SDPath;

public class Square extends Quadrilateral {
	protected final static double TOLERANCE = 0.1d;

	protected final static Square square = new Square();

	Square() {
		super();
	}

	public Square(SDPath path) {
		super(path);
	}

	public Square(AbstractShape shape) {
		super(shape);
	}

	public AbstractShape identify(Quadrilateral shape) {
		List<Point2D> vertices = shape.getPath().getVertices();

		double length = shape.getPath().getOutlineMap().getPerimeter() / 4d;
		Point2D a = vertices.get(3);
		for (Point2D b : vertices) {
			double d = (length - a.distance(b)) / length;
			if (Math.abs(d) > TOLERANCE) {
				return shape;
			}
			a = b;
		}

		return new Square(shape);
	}

	// protected Rectangle getMask(AbstractShape shape) {
	// SDPath path = getPolygon(shape, 4);
	// return new Rectangle(path);
	// }
}
