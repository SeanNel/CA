package ca.shapedetector.shapes;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import math.utils.CriticalPointComparator;
import math.utils.CriticalPoints;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

import ca.Debug;
import ca.shapedetector.distribution.RadialDistance;
import ca.shapedetector.path.SDPath;

public abstract class Polygon extends AbstractShape {

	Polygon() {
		super();
	}

	public Polygon(SDPath path) {
		super(path);
	}

	public Polygon(AbstractShape shape) {
		super(shape);
	}

	/**
	 * Returns a polygonal shape with n vertices/sides that approximates the
	 * specified shape.
	 * 
	 * @param shape
	 * @param s
	 * @return Polygon with s sides.
	 * */
	public SDPath getPolygon(AbstractShape shape, int s) {
		/* Catches vertices that occur at the start point. */
		double x0 = -1.0;
		double x1 = shape.getPath().getPerimeter();
		UnivariateDifferentiableFunction f = shape.getDistribution();

		/* Displays a chart of the shape distribution. */
		if (Debug.debug) {
			graphics.LineChartFrame.frame.setTitle("Shape distribution");
			graphics.LineChartFrame.displayData(x0, x1, f);
		}

		List<Double> indices = getN(f, x0, x1, s);

		SDPath path = new SDPath();
		if (indices.size() >= s) {
			List<Point2D> vertices = shape.getPath().getVertices(indices, s);
			path.addVertices(vertices);
		}
		return path;
	}

	/**
	 * Gets the positions of the n greatest maximum values.
	 * 
	 * @param f
	 * @return
	 */
	protected List<Double> getN(UnivariateDifferentiableFunction f, double x0,
			double x1, int n) {
		if (f == null || x1 <= x0 || n < 1) {
			throw new RuntimeException();
		}
		/* Gets the local critical points */
		CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		List<Double> list;
		if (distribution.getClass() == RadialDistance.class) {
			list = criticalPoints.maxima();
		} else {
			list = criticalPoints.criticalPoints();
		}

		/* Removes points too close to one another */
		double last = -minSideLength - 1.0;
		List<Double> shortlist = new ArrayList<Double>(list);
		for (double x : list) {
			if (x < last + minSideLength) {
				shortlist.remove(x);
			} else {
				last = x;
			}
		}

		if (distribution.getClass() == RadialDistance.class) {
			Collections.sort(shortlist, new CriticalPointComparator(f,
					CriticalPointComparator.MAXIMUM_Y));
		} else {
			Collections.sort(shortlist, new CriticalPointComparator(f,
					CriticalPointComparator.SECOND_DERIVATIVE));
		}

		/* Selects the n points of greatest value */
		if (n > shortlist.size()) {
			n = shortlist.size();
		}
		shortlist = shortlist.subList(0, n);
		Collections.sort(shortlist, new CriticalPointComparator(f,
				CriticalPointComparator.INCREASING_X));

		if (Debug.debug) {
			System.out.println("*");
			criticalPoints.printPoints(shortlist);
		}
		return shortlist;
	}

}
