package ca.shapedetector.shapes;

import ca.shapedetector.blob.Blob;
import ca.shapedetector.path.SDPath;
import exceptions.MethodNotImplementedException;

public class RootShape extends SDShape {

	public RootShape() {
		super();
	}

	protected void loadRelatedShapes() {
		super.loadRelatedShapes();
		relatedShapes.add(new Quadrilateral());
		// relatedShapes.add(new SDEllipse());
	}

	/**
	 * Identifies the shape.
	 * <p>
	 * Assumes that the blob's outline cells have already been arranged in
	 * sequence.
	 * 
	 * @param path
	 *            A path describing the unidentified shape.
	 * @return An instance of the detected shape.
	 * @throws MethodNotImplementedException
	 *             if a shape in relatedShapes does not implement the identify
	 *             method.
	 */
	public SDShape identifyShape(Blob blob)
			throws MethodNotImplementedException {
		SDShape identifiedShape = null;
		SDShape shape = new SDShape(new SDPath(blob));

		for (SDShape relatedShape : relatedShapes) {
			identifiedShape = relatedShape.identify(shape);
			// Input.waitForSpace();

			if (identifiedShape != null) {
				return identifiedShape;
			}
		}
		return new UnknownShape(shape);
	}
}