package ca.rules.protoshape;

import ca.Stopwatch;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.shapes.CAShape;

/**
 * Identifies ProtoShapes as CAShapes.
 */
public class CAProtoShapeIdentifierRule extends CAProtoShapeRule {
	CAShape shapeDetector;
	Stopwatch stopwatch;
	long[] timers;

	public CAProtoShapeIdentifierRule(CAShapeDetector ca) {
		super(ca);
		shapeDetector = new CAShape(ca.getPicture());
		stopwatch = new Stopwatch();
		timers = new long[3];
	}

	public void update(CAProtoShape protoShape) {
		// System.out.println("*** " + protoShape);
		/* Arranges outline cells in order. */
		stopwatch.start();
		// protoShape.orderOutlineCells();
		timers[0] += stopwatch.time();

		/* Calculates gradients. */
		stopwatch.start();
		protoShape.calculateGradients();
		timers[1] += stopwatch.time();

		/* Identifies shapes. */
		stopwatch.start();
		ca.addShape(shapeDetector.identifyShape(protoShape));
		timers[2] += stopwatch.time();
	}

	public void printTimers() {
		System.out.println("Arranged outlines in order: " + timers[0] + " ms");
		System.out.println("Calculated gradients: " + timers[1] + " ms");
		System.out.println("Identified shapes: " + timers[2] + " ms");
	}
}
