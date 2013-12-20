package ca.shapedetector.shapes;

import ca.shapedetector.CAProtoShape;


/**
 * An unrecognized shape.
 * 
 * @author Sean
 */
public class CAUnknownShape extends CAShape {
	
	public CAUnknownShape(CAProtoShape protoShape) {
		super(protoShape);
	}

	protected CAShape identify(CAProtoShape protoShape) {
		return new CAUnknownShape(protoShape);
	}
	
}
