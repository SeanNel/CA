package ca.shapedetector.shapes;

import std.Picture;

public class SDRootShape extends SDShape {
	
	public SDRootShape(Picture picture) {
		super(picture);
	}

	protected void loadRelatedShapes() {
		relatedShapes.add(new SDRectangle(picture));
		// relatedShapes.add(new SDEllipse(graphics));
	}
}