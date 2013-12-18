package ca.shapedetector.shapes;

import ca.shapedetector.CAShape;

public class Rectangle extends CAShape {
	public Rectangle() {
	}
	
	public Rectangle(CAShape shape) {
		super(shape);
	}

	public CAShape detect(CAShape shape) {
		/*
		 * TODO improve on this basic method of detection. Currently this would
		 * only work for non-rotated rectangles and those that do not enclose
		 * other shapes.
		 */
		int areaDifference = (shape.width() * shape.height()) - shape.getArea();
		if (areaDifference < 0)
			areaDifference *= -1;

		float a = (float) areaDifference / (float) shape.getArea();
		if (a <= tolerance) {
			return new Rectangle(shape);
		} else {
			return null;
		}
	}
}
