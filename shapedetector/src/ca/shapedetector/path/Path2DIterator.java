package ca.shapedetector.path;

import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.Iterator;

/**
 * Iterates through the vertices of a Path2D object. Curved segments are
 * approximated with a series of straight segments. It is only able to iterate
 * along the primary direction of the outline.
 * 
 * @author Sean
 */
public class Path2DIterator implements Iterator<Point2D> {
	protected final static double flatness = 1.5d;
	protected PathIterator pathIterator;
	protected boolean hasNext;
	/*
	 * Diagonal pixels are at distance sqrt(2) px, but horizontal and vertical
	 * ones are only 1 px apart.
	 */
	protected double step = 1d;
	protected Point2D currentPosition;
	protected int currentSegment;

	public Path2DIterator(Path2D path) {
		pathIterator = path.getPathIterator(null, flatness);
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
		double[] coordinates = new double[6];
		currentSegment = pathIterator.currentSegment(coordinates);
		return new Point2D.Double(coordinates[0], coordinates[1]);
	}

	protected int getSegmentType() {
		double[] p = new double[6];
		return pathIterator.currentSegment(p);
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
	}
}
