package ca.shapedetector.distribution;

import helpers.Misc;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import math.DiscreteFunction;

import ca.shapedetector.path.InterpolatingVertexIterator;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.path.VertexIterator;

public class Distribution {
	public final static double delta = 1.5;

	public DiscreteFunction compute(SDPath path) {
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
		// double[] indices = new double[n];
		// int i = 0;
		// indices[i] = perimeter;
		double perimeter = 0.0;

		Point2D o = path.getCentroid();
		Point2D a = vertices.get(n - 1);
		while (iterator.hasNext()) {
			Point2D b = iterator.next();

			xList.add(perimeter);
			yList.add(getValue(o, a, b));

			perimeter += a.distance(b);
			a = b;
		}

		double[] abscissae = Misc.toArray(xList);
		double[] ordinates = Misc.toArray(yList);
		if (abscissae.length == 0) {
			abscissae = new double[1];
			ordinates = new double[1];
		}
		return new DiscreteFunction(abscissae, ordinates);
	}

	protected double getValue(Point2D o, Point2D a, Point2D b) {
		return 0.0;
	}

	// protected static double getGradient(Vector v1, Vector v2) {
	// /* Y is negative because it increases from top to bottom. */
	// v1 = new Vector(v1.getX(), -v1.getY());
	// v2 = new Vector(v2.getX(), -v2.getY());
	//
	// double gradient = v2.getAngle() - v1.getAngle();
	// if (gradient < 0) {
	// gradient += 2.0 * Math.PI;
	// }
	// gradient -= (Math.PI / 2.0);
	// return gradient;
	// }
}
