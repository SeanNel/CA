package ca.rules.protoshape;

import ca.Stopwatch;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.SDRootShape;

/**
 * Identifies ProtoShapes as CAShapes.
 */
public class CAProtoShapeIdentifierRule extends CAProtoShapeRule {
	SDRootShape shapeDetector;
	Stopwatch stopwatch;
	long[] timers;

	public CAProtoShapeIdentifierRule(CAShapeDetector ca) {
		super(ca);
		shapeDetector = new SDRootShape(ca.getPicture());
		stopwatch = new Stopwatch();
		timers = new long[3];
	}

	public void update(CAProtoShape protoShape) {
		if (protoShape.getArea() < 16) {
			return;
		}

		stopwatch.start();
		protoShape.arrangeOutlineCells();
		timers[0] += stopwatch.time();

		stopwatch.start();
		SDPath path = new SDPath(protoShape);
		if (path.getArea() > 16) {
			ca.addShape(shapeDetector.identifyShape(path));
//			Input.waitForSpace();
		}
		timers[2] += stopwatch.time();
	}

	public void printTimers() {
		System.out.println("Arranged outlines in order: " + timers[0] + " ms");
		// System.out.println("Calculated gradients: " + timers[1] + " ms");
		System.out.println("Identified shapes: " + timers[2] + " ms");
	}
}
