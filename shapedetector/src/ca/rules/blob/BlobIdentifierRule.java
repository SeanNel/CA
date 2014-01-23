package ca.rules.blob;

import helpers.Stopwatch;
import ca.shapedetector.BlobMap;
import ca.shapedetector.ShapeList;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.shapes.RootShape;
import exceptions.CAException;

/**
 * Identifies blobs as Shapes.
 */
public class BlobIdentifierRule<V> extends BlobRule<V> {
	protected final RootShape<V> shapeDetector = new RootShape<V>();
	protected final ShapeList shapeList;
	protected final Stopwatch stopwatch;
	protected final long[] timers;

	public BlobIdentifierRule(final BlobMap<V> blobMap, final ShapeList shapeList) {
		super(blobMap);
		this.shapeList = shapeList;
		stopwatch = new Stopwatch();
		timers = new long[3];
	}

	@Override
	public void update(final Blob<V> blob) throws CAException {
		if (blob.getArea() < 16) {
			return;
		}

		stopwatch.start();

		// System.out.println("Unarranged outline cells:");
		// System.out.println(blob.getOutlineCells());
		blob.arrangeOutlineCells();
		// System.out.println("Arranged outline cells:");
		// System.out.println(blob.getOutlineCells());

		timers[0] += stopwatch.time();

		if (blob.getOutlineCells().size() < 16) {
			return;
		}

		stopwatch.start();
		shapeList.addShape(shapeDetector.identify(blob));
		// Input.waitForSpace();
		timers[2] += stopwatch.time();
	}

	@Override
	public void complete() {
		printTimers();
	}

	public void printTimers() {
		System.out.println("Arranged outlines in order: " + timers[0] + " ms");
		// System.out.println("Calculated gradients: " + timers[1] + " ms");
		System.out.println("Identified shapes: " + timers[2] + " ms");
	}
}
