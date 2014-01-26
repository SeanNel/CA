package ca.shapedetector.distribution;

import helpers.Misc;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;

import math.DiscreteFunction;

import ca.shapedetector.path.InterpolatingVertexIterator;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.path.VertexIterator;

/**
 * Computes a function that describes the outline of a path.
 * 
 * @author Sean
 */
public abstract class Distribution {
	public final static double delta = 1.5d;

	public UnivariateFunction compute(final SDPath path) {
		List<Point2D> vertices = path.getVertices();
		InterpolatingVertexIterator iterator = new InterpolatingVertexIterator(
				vertices, VertexIterator.FORWARD, 1.5);

		int n = vertices.size();
		List<Double> xList = new ArrayList<Double>(n);
		List<Double> yList = new ArrayList<Double>(n);

		/*
		 * Maps each uninterpolated vertex to its distance from the starting
		 * point (along the path).
		 */
		double perimeter = 0.0;

		Point2D o = path.getCentroid();
		Point2D a = vertices.get(n - 1);
		while (iterator.hasNext()) {
			Point2D b = iterator.next();
			double distance = a.distance(b);
			if (distance > 0) {
				perimeter += a.distance(b);

				xList.add(perimeter);
				yList.add(getValue(o, a, b));

				a = b;
			}
		}

		double[] abscissae = Misc.toArray(xList);
		double[] ordinates = Misc.toArray(yList);
		if (abscissae.length == 0) {
			abscissae = new double[1];
			ordinates = new double[1];
		}
		DiscreteFunction f = new DiscreteFunction(abscissae, ordinates);
		// return f;
		return filter(f);
	}

	protected double getValue(final Point2D o, final Point2D a, final Point2D b) {
		return 0d;
	}

	/**
	 * Filters noise.
	 * 
	 * @param f
	 * @return
	 */
	public UnivariateFunction filter(final DiscreteFunction f) {
		return f;
	}
}
