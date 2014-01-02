package ca.shapedetector.shapes;

/**
 * An unrecognized shape.
 * 
 * @author Sean
 */
public class SDUnknownShape extends SDShape {

	public SDUnknownShape(SDShape shape) {
		super(shape);
	}

	protected SDShape identify(SDShape shape) {
		return null;
	}

}
