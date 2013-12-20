package ca.shapedetector.shapes;

import ca.shapedetector.CAProtoShape;

public class CASquare extends CARectangle {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.1;

	private int width;

	public CASquare() {
	}

	public CASquare(CAProtoShape protoShape) {
		super(protoShape);
	}

	protected CAShape identify(CAProtoShape protoShape) {
		/*
		 * TODO improve on this basic method of detection. Currently this would
		 * only work for non-rotated squares and those that do not enclose other
		 * shapes.
		 */

		int[] dimensions = protoShape.getDimensions();
		int lengthDifference = dimensions[1] - dimensions[0];
		if (lengthDifference < 0)
			lengthDifference *= -1;

		double a = lengthDifference * lengthDifference
				/ (double) protoShape.getArea();
		// System.out.println(a + ": " + protoShape.getArea());

		if (a <= tolerance) {
			CASquare square = new CASquare(protoShape);
			square.width = dimensions[0] + dimensions[1] / 2;
			return square;
		} else {
			return null;
		}
	}

	protected String getStats() {
		return "w=" + width;
	}
}
