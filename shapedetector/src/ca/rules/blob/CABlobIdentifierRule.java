package ca.rules.blob;

import ca.Stopwatch;
import ca.shapedetector.CABlob;
import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.shapes.SDRootShape;

/**
 * Identifies blobs as CAShapes.
 */
public class CABlobIdentifierRule extends CABlobRule {
	static final SDRootShape shapeDetector = new SDRootShape();
	Stopwatch stopwatch;
	long[] timers;

	public CABlobIdentifierRule(CAShapeDetector ca) {
		super(ca);
		stopwatch = new Stopwatch();
		timers = new long[3];
	}

	public void update(CABlob blob) {
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
		ca.addShape(shapeDetector.identifyShape(blob, ca));
		// Input.waitForSpace();
		timers[2] += stopwatch.time();
	}

	public void printTimers() {
		System.out.println("Arranged outlines in order: " + timers[0] + " ms");
		// System.out.println("Calculated gradients: " + timers[1] + " ms");
		System.out.println("Identified shapes: " + timers[2] + " ms");
	}
}
