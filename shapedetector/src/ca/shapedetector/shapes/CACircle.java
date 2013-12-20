package ca.shapedetector.shapes;

import ca.shapedetector.CAProtoShape;

public class CACircle extends CAEllipse {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 4.0;

	private int radius;

	/**
	 * Singleton constructor.
	 */
	public CACircle() {
	}

	public CACircle(CAProtoShape protoShape) {
		super(protoShape);
	}

	protected CAShape identify(CAProtoShape protoShape) {
		/*
		 * TODO improve on this basic method of detection. Currently this would
		 * only work for non-rotated rectangles and those that do not enclose
		 * other shapes.
		 */
		int[] dimensions = protoShape.getDimensions();
		double r = (dimensions[1] + dimensions[0]) / 2.0;
		double areaDifference = Math.PI * r * r - protoShape.getArea();
		if (areaDifference < 0) {
			areaDifference *= -1;
		}

		double a = areaDifference / (double) protoShape.getArea();
//		System.out.println(a);
		if (a <= tolerance) {
			CACircle circle = new CACircle(protoShape);
			circle.radius = dimensions[0] / 2;
			return circle;
		} else {
			return null;
		}
	}

	protected String getStats() {
		return "r=" + radius;
	}
}
