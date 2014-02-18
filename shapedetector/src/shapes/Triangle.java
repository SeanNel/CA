package shapes;

import path.SDPath;

public class Triangle extends Polygon {
	protected final static double TOLERANCE = 0.3d;

	Triangle() {
		super(TOLERANCE);
	}

	public Triangle(final SDPath path) {
		super(path, TOLERANCE);
	}

	public Triangle(final AbstractShape shape) {
		super(shape);
	}

	protected Triangle getMask(final AbstractShape shape) {
		SDPath path = getPolygon(shape, 3);
		return new Triangle(path);
	}
}
