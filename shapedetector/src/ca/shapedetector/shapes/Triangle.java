package ca.shapedetector.shapes;

import ca.shapedetector.path.SDPath;

public class Triangle extends Polygon {
	protected final static double TOLERANCE = 0.3d;

	Triangle() {
		super(DISTRIBUTION, TOLERANCE);
	}

	public Triangle(final SDPath path) {
		super(path, DISTRIBUTION, TOLERANCE);
	}

	public Triangle(final AbstractShape shape) {
		super(shape);
	}

	protected Triangle getMask(final AbstractShape shape) {
		SDPath path = getPolygon(shape, 3);
		return new Triangle(path);
	}
}
