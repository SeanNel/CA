package ca.shapedetector.shapes;

import ca.Debug;
import ca.shapedetector.path.SDPath;

public class Quadrilateral extends Polygon {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected final static double tolerance = 0.3;

	Quadrilateral() {
		super();
	}

	public Quadrilateral(SDPath path) {
		super(path);
	}

	public Quadrilateral(AbstractShape shape) {
		super(shape);
	}

	public AbstractShape identify(AbstractShape shape) {
		if (shape == null) {
			throw new RuntimeException();
		}
		/* For debugging */
		if (Debug.debug) {
			Debug.displayActiveShape(shape);
		}

		Quadrilateral mask = getMask(shape);
		if (mask == null) {
			return null;
		}

		/* For debugging */
		if (Debug.debug) {
			Debug.displayMaskShape(shape, mask);
		}

		double match = mask.compare(shape);
		/* Input.waitforSpace() */

		if (1.0 - match < tolerance) {
			shape = new Quadrilateral(mask);
//			shape = new Quadrilateral(shape);
			// shape = Rectangle.identify(shape);
		}
		return shape;
	}

	protected Quadrilateral getMask(AbstractShape shape) {
		/* Creates mask shape and compares the two. */
		SDPath path = getPolygon(shape, 4);
		return new Quadrilateral(path);
	}
}
