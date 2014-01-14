package ca.rules.shape;

import ca.rules.Rule;
import ca.shapedetector.ShapeList;
import ca.shapedetector.shapes.AbstractShape;
import exceptions.CAException;

public abstract class ShapeRule implements Rule<AbstractShape> {
	protected final ShapeList shapeList;

	/**
	 * Constructor.
	 * 
	 * @param shapeList
	 */
	public ShapeRule(ShapeList shapeList) {
		this.shapeList = shapeList;
	}
	
	public void start() throws CAException {
		/* Method stub. */
	}

	// public void update(AbstractShape shape) throws CAException {
	// /* Method stub. */
	// }
	
	public void end() throws CAException {
		/* Method stub. */
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
