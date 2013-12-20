package ca.shapedetector.shapes;

import ca.shapedetector.CAProtoShape;

public class CAEllipse extends CAShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static float tolerance = 0.15f;

	protected static final CACircle circle = new CACircle();

	private int length;
	private int width;

	/**
	 * Singleton constructor.
	 */
	public CAEllipse() {
	}

	public CAEllipse(CAProtoShape protoShape) {
		super(protoShape);
	}

	protected CAShape identify(CAProtoShape protoShape) {
		/*
		 * TODO 
		 */
		int[] dimensions = protoShape.getDimensions();
		int areaDifference = (int) ((Math.PI * dimensions[1] * dimensions[0]) - protoShape
				.getArea());
		if (areaDifference < 0) {
			areaDifference *= -1;
		}

		float a = (float) areaDifference / (float) protoShape.getArea();
		if (a <= tolerance) {
			CAShape shape = circle.identify(protoShape);
			if (shape != null) {
				return shape;
			} else {
				CAEllipse ellipse = new CAEllipse(protoShape);
				if (dimensions[0] > dimensions[1]) {
					ellipse.length = dimensions[0];
					ellipse.width = dimensions[1];
				} else {
					ellipse.length = dimensions[1];
					ellipse.width = dimensions[0];
				}
				return ellipse;
			}
		} else {
			return null;
		}
	}

	protected String getStats() {
		return "l=" + length + ", w=" + width;
	}
}
