package ca.shapedetector.shapes;

import javax.swing.JFrame;

import ca.shapedetector.CABlob;
import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.path.SDPath;

public class SDRootShape extends SDShape {

	public SDRootShape() {
		super();
	}

	protected void loadRelatedShapes() {
		super.loadRelatedShapes();
		relatedShapes.add(new SDQuadrilateral());
		// relatedShapes.add(new SDEllipse());
	}

	static final JFrame frame = new JFrame();

	/**
	 * Identifies the shape.
	 * <p>
	 * Assumes that the blob's outline cells have already been arranged in
	 * sequence.
	 * 
	 * @param path
	 *            A path describing the unidentified shape.
	 * @return An instance of the detected shape.
	 */
	public SDShape identifyShape(CABlob blob, CAShapeDetector ca) {
		SDShape identifiedShape = null;
		SDShape shape = new SDShape(new SDPath(blob));

		if (showActiveShape) {
			shape.path.display(frame);
		}

		for (SDShape relatedShape : relatedShapes) {
			identifiedShape = relatedShape.identify(shape);
			// Input.waitForSpace();

			if (identifiedShape != null) {
				return identifiedShape;
			}
		}
		return new SDUnknownShape(shape);
	}
}