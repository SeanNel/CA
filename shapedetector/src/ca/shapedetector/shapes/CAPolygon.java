package ca.shapedetector.shapes;

import ca.shapedetector.CAProtoShape;

public class CAPolygon extends CAShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static float tolerance = 0.1f;

	/**
	 * Singleton constructor.
	 */
	public CAPolygon() {
	}

	public CAPolygon(CAProtoShape protoShape) {
		super(protoShape);
	}

	protected CAShape identify(CAShape shape) {
		/* TODO: method stub */
		return null;
	}
}
