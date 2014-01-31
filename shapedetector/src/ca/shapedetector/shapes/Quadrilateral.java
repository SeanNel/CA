package ca.shapedetector.shapes;

import ca.shapedetector.path.SDPath;

public class Quadrilateral extends Polygon {
	protected final static double TOLERANCE = 0.2d;
	protected final static Rectangle rectangle = new Rectangle();

	Quadrilateral() {
		super(null, DISTRIBUTION, TOLERANCE);
	}

	public Quadrilateral(SDPath path) {
		super(path, DISTRIBUTION, TOLERANCE);
	}

	public Quadrilateral(AbstractShape shape) {
		super(shape);
	}

	@Override
	protected Quadrilateral getMask(final AbstractShape shape) {
		SDPath path = getPolygon(shape, 4);
		return new Quadrilateral(path);
	}

	@Override
	protected AbstractShape identifySubclass() {
		 AbstractShape shape = rectangle.identify(this);
//		AbstractShape shape = this;
		return shape;
	}

}
