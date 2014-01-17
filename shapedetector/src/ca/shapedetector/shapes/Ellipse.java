package ca.shapedetector.shapes;

import helpers.Misc;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import math.utils.CriticalPoints;

import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

import ca.shapedetector.distribution.Distribution;
import ca.shapedetector.path.SDPath;

public class Ellipse extends AbstractShape {
	protected final static double TOLERANCE = 0.13; // Polygon.TOLERANCE;
	/*
	 * This class assumes that this is the distribution method when it creates
	 * the ellipse mask.
	 */
	protected final static Distribution DISTRIBUTION = RADIAL_DISTANCE;

	protected final static Circle circle = new Circle();

	Ellipse() {
		super(null, DISTRIBUTION, TOLERANCE);
	}

	public Ellipse(SDPath path) {
		super(path, DISTRIBUTION, TOLERANCE);
	}

	public Ellipse(AbstractShape shape) {
		super(shape);
	}

	/* Creates an ellipse that approximates the shape. */
	protected Ellipse getMask(AbstractShape shape) {
		SDPath path = getEllipse(shape);
		return new Ellipse(path);
	}

	@Override
	protected AbstractShape identifySubclass() {
		AbstractShape shape = circle.identify(this);
		return shape;
	}

	public SDPath getEllipse(AbstractShape shape) {
		/* Catches vertices that occur at the start point. */
		double x0 = -1.0;
		double x1 = shape.getPath().getPerimeter();
		UnivariateDifferentiableFunction f = shape.getDistribution(distributionType);

		/* Gets maximum and minimum distance from centroid. */
		CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		List<Double> indices = new ArrayList<Double>(2);
		indices.add(criticalPoints.maximum());
		indices.add(criticalPoints.minimum());

		/* Plots an ellipse. */
		SDPath path = new SDPath();
		List<Point2D> vertices = shape.getPath().getVertices(indices);
		Point2D centroid = shape.getPath().getCentroid();
		Point2D a = vertices.get(0);
		Point2D b = vertices.get(1);
		double w = centroid.distance(a) * 2d;
		double h = centroid.distance(b) * 2d;
		Shape ellipse = new Ellipse2D.Double(-w / 2d, -h / 2d, w, h);

		/*
		 * Rotates the widest part of the ellipse to line up with the vertex of
		 * the target shape that is furthest from the centroid.
		 */
		double theta = Misc.getAngle(centroid.getX() - a.getX(),
				centroid.getY() - a.getY());

		Path2D ellipsePath = new Path2D.Double(ellipse);
		ellipsePath.transform(AffineTransform.getRotateInstance(theta));
		ellipsePath.transform(AffineTransform.getTranslateInstance(
				centroid.getX(), centroid.getY()));

		path = new SDPath(ellipsePath);
		return path;
	}
}
