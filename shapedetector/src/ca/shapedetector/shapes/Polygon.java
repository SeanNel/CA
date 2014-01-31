package ca.shapedetector.shapes;

import java.awt.geom.Point2D;
import java.util.List;

import math.utils.CriticalPointComparator;
import math.utils.CriticalPoints;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

import ca.shapedetector.ShapeDetector;
import ca.shapedetector.distribution.Distribution;
import ca.shapedetector.path.SDPath;

public abstract class Polygon extends AbstractShape {
	/**
	 * Finding vertices where the gradient changes would be ideal, but the
	 * quantization of angles between the pairs of adjacent pixels causes
	 * problems.
	 */
	// protected final static Distribution DISTRIBUTION = GRADIENT_DISTRIBUTION;
	/**
	 * This method suffers from some limitations, but it exploits the distance
	 * between the centroid and the outline to help create a smooth distribution
	 * graph.
	 */
	protected final static Distribution DISTRIBUTION = RADIAL_DISTANCE;
	protected final static double TOLERANCE = DEFAULT_TOLERANCE;
	/** Minimum side length */
	protected double minSideLength = 3.0;

	Polygon(Distribution distribution, double tolerance) {
		super(null, distribution, tolerance);
	}

	public Polygon(final SDPath path, final Distribution distribution,
			final double tolerance) {
		super(path, distribution, tolerance);
	}

	public Polygon(AbstractShape shape) {
		super(shape);
	}

	/**
	 * Returns a polygonal shape with n vertices/sides that approximates the
	 * specified shape.
	 * 
	 * @param shape
	 *            The shape to approximate.
	 * @param s
	 *            Number of vertices.
	 * @return Polygon with s sides.
	 * */
	public SDPath getPolygon(final AbstractShape shape, final int s) {
		/* Catches vertices that occur at the start point. */
		double x0 = -1.0d;
		double x1 = shape.getPath().getPerimeter();
		UnivariateDifferentiableFunction f = shape
				.getDistribution(distributionType);

		if (ShapeDetector.debug) {
			graphics.LineChartFrame.displayData(x0, x1, f);
		}

		int comparisonType;
		if (distributionType == RADIAL_DISTANCE) {
			comparisonType = CriticalPointComparator.MAXIMUM_Y;
		} else {
			comparisonType = CriticalPointComparator.SECOND_DERIVATIVE;
		}
		CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		List<Double> indices = criticalPoints.significantPoints(comparisonType,
				s, minSideLength);

		// System.out.println("Critical points *** ");
		// for (Double d : criticalPoints.criticalPoints()) {
		// System.out.println(">>> " + d);
		// }

		// System.out.println("Significant points *** ");
		// for (Double d : indices) {
		// System.out.println(">>> " + d);
		// }

		List<Point2D> vertices = shape.getPath().getVertices(indices);
		return new SDPath(vertices);
	}

}
