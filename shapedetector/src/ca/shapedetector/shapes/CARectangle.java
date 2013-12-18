package ca.shapedetector.shapes;


public class CARectangle extends CAShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	public static float tolerance = 0.2f;
	
	public CARectangle() {
	}
	
	public CARectangle(CAShape shape) {
		super(shape);
	}

	public CAShape detect(CAShape shape) {
		/*
		 * TODO improve on this basic method of detection. Currently this would
		 * only work for non-rotated rectangles and those that do not enclose
		 * other shapes.
		 */
		int areaDifference = (shape.getWidth() * shape.getHeight()) - shape.getArea();
		if (areaDifference < 0)
			areaDifference *= -1;

		float a = (float) areaDifference / (float) shape.getArea();
		if (a <= tolerance) {
			return new CARectangle(shape);
		} else {
			return null;
		}
	}
}
