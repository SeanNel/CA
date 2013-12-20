package ca.shapedetector.shapes;

import ca.shapedetector.CAProtoShape;

public class CARectangle extends CAPolygon {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.15;

	protected static final CASquare square = new CASquare();

	private int length;
	private int width;

	/**
	 * Singleton constructor.
	 */
	public CARectangle() {
	}

	public CARectangle(CAProtoShape protoShape) {
		super(protoShape);
	}

	protected CAShape identify(CAProtoShape protoShape) {
		/*
		 * TODO improve on this basic method of detection. Currently this would
		 * only work for non-rotated rectangles and those that do not enclose
		 * other shapes.
		 */
		int[] dimensions = protoShape.getDimensions();
		double areaDifference = dimensions[1] * dimensions[0]
				- protoShape.getArea();
		if (areaDifference < 0) {
			areaDifference *= -1;
		}

		double a = areaDifference / (double) protoShape.getArea();
		if (a <= tolerance) {
			CAShape shape = square.identify(protoShape);
			if (shape != null) {
				return shape;
			} else {
				CARectangle rectangle = new CARectangle(protoShape);
				if (dimensions[0] > dimensions[1]) {
					rectangle.length = dimensions[0];
					rectangle.width = dimensions[1];
				} else {
					rectangle.length = dimensions[1];
					rectangle.width = dimensions[0];
				}
				return rectangle;
			}
		} else {
			return null;
		}
	}

	protected String getStats() {
		return "l=" + length + ", w=" + width;
	}
}
