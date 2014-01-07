package ca.rules.blob;

import helpers.Stopwatch;
import ca.shapedetector.BlobMap;
import ca.shapedetector.ShapeList;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.shapes.RootShape;
import exceptions.CAException;

/**
 * Identifies blobs as CAShapes.
 */
public class BlobIdentifierRule extends BlobRule {
	static final RootShape shapeDetector = new RootShape();
	protected ShapeList shapeList;
	Stopwatch stopwatch;
	long[] timers;

	public BlobIdentifierRule(BlobMap blobMap, ShapeList shapeList) {
		super(blobMap);
		this.shapeList = shapeList;
		stopwatch = new Stopwatch();
		timers = new long[3];
	}

	public void update(Blob blob) throws CAException {
		if (blob.getArea() < 16) {
			return;
		}

		stopwatch.start();
		blob.arrangeOutlineCells();
		timers[0] += stopwatch.time();

		if (blob.getOutlineCells().size() < 16) {
			return;
		}

		stopwatch.start();
		shapeList.addShape(shapeDetector.identifyShape(blob));
		// Input.waitForSpace();
		timers[2] += stopwatch.time();
	}

	public void printTimers() {
		System.out.println("Arranged outlines in order: " + timers[0] + " ms");
		// System.out.println("Calculated gradients: " + timers[1] + " ms");
		System.out.println("Identified shapes: " + timers[2] + " ms");
	}
}
