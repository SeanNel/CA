package ca.shapedetector.shapes;

import java.awt.Graphics2D;

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

	public SDUnknownShape(SDPath path, Graphics2D graphics) {
		super(path, graphics);
	}

	protected SDShape identify(SDPath path) {
		return null;
	}

}
