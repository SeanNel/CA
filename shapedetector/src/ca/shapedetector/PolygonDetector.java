package ca.shapedetector;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import math.utils.CriticalPointComparator;
import math.utils.CriticalPoints;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

import ca.Debug;
import ca.shapedetector.path.OutlineMap;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.AbstractShape;

public class PolygonDetector {
	/** Minimum side length */
	protected final static double delta = 3.0;

	/**
	 * Returns a polygonal shape with n vertices/sides that approximates the
	 * specified shape.
	 * 
	 * @param shape
	 * @param s
	 * @return Polygon with s sides.
	 * */
	public static SDPath getPolygon(AbstractShape shape, int s) {
		double x0 = -1.0; // 0.0;
		double x1 = shape.getPath().getPerimeter();
		UnivariateDifferentiableFunction f = shape.getDistribution();

		/* For debugging, displays a chart of the shape distribution. */
		if (Debug.debug) {
			graphics.LineChartFrame.frame.setTitle("Shape distribution");
			graphics.LineChartFrame.displayData(x0, x1, f);
		}

		List<Double> indices = getNMax(f, x0, x1, s);

		SDPath path = new SDPath();
		if (indices.size() >= s) {
			List<Point2D> vertices = getVertices(shape.getPath(), indices, s);
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
	private static List<Double> getNMax(UnivariateDifferentiableFunction f,
			double x0, double x1, int n) {
		if (f == null || x1 <= x0 || n < 1) {
			throw new RuntimeException();
		}
		/* Gets the local maxima */
		CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		// List<Double> list = criticalPoints.criticalPoints();
		List<Double> list = criticalPoints.maxima();

		/* Removes points too close to one another */
		double last = -delta - 1.0;
		List<Double> shortlist = new ArrayList<Double>(list);
		for (double x : list) {
			if (x < last + delta) {
				shortlist.remove(x);
			} else {
				last = x;
			}
		}

		Collections.sort(shortlist, new CriticalPointComparator(f,
				CriticalPointComparator.MAXIMUM));

		/* Selects the n points of greatest value */
		if (n > shortlist.size()) {
			n = shortlist.size();
		}
		shortlist = shortlist.subList(0, n);
		Collections.sort(shortlist, new CriticalPointComparator(f,
				CriticalPointComparator.INCREASING));

		if (Debug.debug) {
			System.out.println("*");
			criticalPoints.printPoints(shortlist);
		}
		return shortlist;
	}

	protected static List<Point2D> getVertices(SDPath path,
			List<Double> indices, int s) {

		OutlineMap outlineMap = path.getOutlineMap();
		int n = indices.size();
		List<Point2D> vertices = new ArrayList<Point2D>(n);

		for (int i = 0; i < n; i++) {
			Point2D vertex = outlineMap.getVertex(indices.get(i));
			vertices.add(vertex);
		}
		return vertices;
	}

	// protected static Shape plotPolygon(List<Point2D> vertices) {
	// Path2D path = new Path2D.Double();
	// Iterator<Point2D> iterator = vertices.iterator();
	// Point2D vertex = iterator.next();
	// path.moveTo(vertex.getX(), vertex.getY());
	//
	// for (int i = 1; i < vertices.size(); i++) {
	// vertex = iterator.next();
	// path.lineTo(vertex.getX(), vertex.getY());
	// }
	//
	// iterator = vertices.iterator();
	// vertex = iterator.next();
	// path.lineTo(vertex.getX(), vertex.getY());
	// path.closePath();
	//
	// return path;
	// }
}
