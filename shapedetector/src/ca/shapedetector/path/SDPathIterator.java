package ca.shapedetector.path;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Iterates through the path, assuming that the path is a polygonal closed loop.
 * 
 * @author Sean
 */
public class SDPathIterator implements Iterator<Point2D> {
	protected PathIterator pathIterator;
	protected boolean hasNext;
	/* Interpolates diagonal pixels at step < sqrt(2) */
	protected double step = 1.0;
	protected Point2D currentPosition;
	protected int currentSegment;

	public SDPathIterator(Path2D path) {
		pathIterator = path.getPathIterator(null);
		hasNext = !pathIterator.isDone();
		currentPosition = getSegmentCoordinates();
	}

	@Override
	public boolean hasNext() {
		return hasNext;
	}

	@Override
	public Point2D next() {
		currentPosition = nextStep();
		return currentPosition;
	}

	protected Point2D nextStep() {
		Point2D nextPosition = getSegmentCoordinates();

		/* Interpolates points between vertices further apart than step length. */
		// double[] v = { nextPosition[0] - currentPosition[0],
		// nextPosition[1] - currentPosition[1] };
		// double modulus = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
		// if (modulus > step) {
		// nextPosition[0] = currentPosition[0] + (v[0] / modulus * step);
		// nextPosition[1] = currentPosition[1] + (v[1] / modulus * step);
		// } else {
		pathIterator.next();
		hasNext = !pathIterator.isDone();

		if (getSegmentType() == PathIterator.SEG_CLOSE) {
			hasNext = false;
		}
		// }

		return nextPosition;
	}

	protected Point2D getSegmentCoordinates() {
		double[] coordinates = new double[5];
		currentSegment = pathIterator.currentSegment(coordinates);
		return new Point2D.Double(coordinates[0], coordinates[1]);
	}

	protected int getSegmentType() {
		double[] p = new double[5];
		return pathIterator.currentSegment(p);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}
}
