package ca.shapedetector.path;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.util.Iterator;

/**
 * Iterates through the path, assuming that the path is a polygonal closed loop.
 * Adds vertices along each step, but does not guarantee that all vertices will
 * be equally spaced (due to corner vertices).
 * 
 * @author Sean
 * 
 */
public class SDPathIterator implements Iterator<double[]> {
	protected PathIterator pathIterator;
	protected boolean hasNext;
	protected double step = 1.0;
	protected double[] currentPosition;
	protected int currentSegment;

	// protected boolean strict = false;

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
	public double[] next() {
		currentPosition = nextStep();

		if (getSegmentType() == PathIterator.SEG_CLOSE) {
			hasNext = false;
		}

		return currentPosition;
	}

	protected double[] nextStep() {
		double[] nextPosition = getSegmentCoordinates();

		/* Interpolates points between vertices further apart than step length. */
		double[] v = { nextPosition[0] - currentPosition[0],
				nextPosition[1] - currentPosition[1] };
		double modulus = Math.sqrt(v[0] * v[0] + v[1] * v[1]);
		if (modulus > step) {
			nextPosition[0] = currentPosition[0]
					+ (v[0] / modulus * step);
			nextPosition[1] = currentPosition[1]
					+ (v[1] / modulus * step);
		} else {
			pathIterator.next();
			hasNext = !pathIterator.isDone();
		}

		return nextPosition;
	}

	protected double[] getSegmentCoordinates() {
		double[] currentPosition = new double[5];
		currentSegment = pathIterator.currentSegment(currentPosition);
		double[] coordinates = { currentPosition[0], currentPosition[1] };
		return coordinates;
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
