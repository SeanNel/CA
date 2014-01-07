package ca.rules.shape;

import ca.rules.Rule;
import ca.shapedetector.ShapeList;
import ca.shapedetector.shapes.SDShape;

public abstract class ShapeRule implements Rule<SDShape> {
	protected ShapeList shapeList;

	/**
	 * Constructor.
	 * 
	 * @param shapeList
	 */
	public ShapeRule(ShapeList shapeList) {
		this.shapeList = shapeList;
	}
	
	public void start() {
		/* Method stub. */
	}

	public void update(SDShape shape) {
		/* Method stub. */
	}
	
	public void end() {
		/* Method stub. */
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
