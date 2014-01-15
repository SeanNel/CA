package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;

import math.functions.CyclicSimilarity;
import math.functions.PeriodicDifferentiableFunction;
import math.utils.CriticalPoints;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.util.FastMath;

import ca.Debug;
import ca.shapedetector.distribution.Distribution;
import ca.shapedetector.distribution.RadialDistance;
import ca.shapedetector.path.SDPath;

/**
 * A shape derived from the outlineCells of a blob found by CAShapeDetector.
 * 
 * @author Sean
 */
public abstract class AbstractShape implements SDShape {
	/** Path that defines this shape as a polygon. */
	protected final SDPath path;
	/** The method of collection distribution data. */
	protected final static Distribution distribution = new RadialDistance(); // RadialGradient();
	/** The shape distribution function. */
	protected final UnivariateDifferentiableFunction distributionFunction;
	/** Minimum side length */
	protected double minSideLength = 3.0;

	/**
	 * Constructor for RootShape.
	 */
	protected AbstractShape() {
		path = null;
		distributionFunction = null;
	}

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path that describes this shape.
	 */
	public AbstractShape(SDPath path) {
		this.path = path;
		distributionFunction = loadShapeDistribution();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param shape
	 */
	public AbstractShape(AbstractShape shape) {
		path = new SDPath(shape.path);
		distributionFunction = shape.distributionFunction;
	}

	/**
	 * Gets the path that describes this shape's outline.
	 * 
	 * @return
	 */
	public SDPath getPath() {
		return path;
	}

	/**
	 * Gets the shape distribution.
	 * 
	 * @return
	 */
	public UnivariateDifferentiableFunction getDistribution() {
		return distributionFunction;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "(" + this.getClass().getSimpleName() + ") [" + getDescription()
				+ ", centroid: " + path.getCentroid();
	}

	/**
	 * Gets a text label for the shape.
	 * 
	 * @return
	 */
	protected String getDescription() {
		Rectangle2D bounds = path.getBounds();
		return "w=" + bounds.getWidth() + ", h=" + bounds.getHeight();
	}

	protected UnivariateDifferentiableFunction loadShapeDistribution() {
		double x0 = 0;
		double x1 = path.getPerimeter();
		if (x1 < 3.0) {
			return new Constant(0.0);
		}
		UnivariateFunction f = distribution.compute(path);
		return new PeriodicDifferentiableFunction(f, x0, x1);
	}

	/**
	 * Calculates the difference ratio between this shape and the specified
	 * shape.
	 * 
	 * @param shape
	 *            The shape to compare this shape to.
	 * @return The difference ratio.
	 */
	public double compare(AbstractShape shape) {
		// return compareByDistribution(shape);
		return compareByArea(shape);
	}

	protected double compareByPerimeter(AbstractShape shape) {
		double a = shape.getPath().getPerimeter();
		double b = path.getPerimeter();
		return FastMath.min(a, b) / FastMath.max(a, b);
	}

	protected double compareByArea(AbstractShape shape) {
		double a = shape.getPath().getArea();
		double b = path.getArea();
		return FastMath.min(a, b) / FastMath.max(a, b);
	}

	/* This method also has its problems... */
	protected double compareByDistribution(AbstractShape shape) {
		double x0 = 0;
		double x1 = shape.getPath().getPerimeter();
		if (x1 - x0 <= 0) {
			return 0.0;
		}
		UnivariateDifferentiableFunction f1 = shape.distributionFunction;
		UnivariateDifferentiableFunction f2 = distributionFunction;

		// x1 = 100;
		// UnivariateFunction g1 = new StretchFunction(distribution, x0,
		// perimeter, x0, x1);
		// UnivariateFunction g2 = new StretchFunction(shape.distribution, x0,
		// shape.perimeter, x0, x1);
		// UnivariateDifferentiableFunction f1 =
		// differentiator.differentiate(g1);
		// UnivariateDifferentiableFunction f2 =
		// differentiator.differentiate(g2);

		UnivariateDifferentiableFunction f = new CyclicSimilarity(f1, f2, 1,
				x0, x1);

		CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		double similarity = f.value(criticalPoints.maximum());

		if (Debug.debug) {
			System.out.println(similarity);
			graphics.LineChartFrame.frame.setTitle("Shape distribution");
			graphics.LineChartFrame.displayData(x0, x1, f1, f2, f);
		}
		return similarity;
	}

}