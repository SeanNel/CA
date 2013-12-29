package ca.shapedetector.shapes;

import ca.shapedetector.path.SDPath;
import std.Picture;

/**
 * An unrecognized shape.
 * 
 * @author Sean
 */
public class SDUnknownShape extends SDShape {

	/**
	 * Singleton constructor.
	 */
	public SDUnknownShape() {
	}

	public SDUnknownShape(SDPath path, Picture picture) {
		super(path, picture);
	}

	protected SDShape identify(SDPath path) {
		return null;
	}

}
