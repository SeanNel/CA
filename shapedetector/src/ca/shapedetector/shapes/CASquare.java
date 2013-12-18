package ca.shapedetector.shapes;

public class CASquare extends CARectangle {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	public static float tolerance = 0.1f;

	public CASquare() {
	}

	public CASquare(CAShape shape) {
		super(shape);
	}

	public CAShape detect(CAShape shape) {
		/*
		 * TODO improve on this basic method of detection. Currently this would
		 * only work for non-rotated squares and those that do not enclose other
		 * shapes.
		 */
		CAShape detectedShape = super.detect(shape);
		if (detectedShape == null)
			return null;

		float lengthDifference = shape.getWidth() - shape.getHeight();
		if (lengthDifference < 0)
			lengthDifference *= -1;

		float a = (float) (lengthDifference * lengthDifference)
				/ (float) shape.getArea();
		// System.out.println(a);
		if (a < tolerance) {
			return new CASquare(shape);
		} else {
			return null;
		}
	}
}
