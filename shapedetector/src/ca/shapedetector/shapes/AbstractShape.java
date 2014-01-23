package ca.shapedetector.shapes;

import graphics.SDPanel;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import math.functions.CyclicSimilarity;
import math.functions.PeriodicDifferentiableFunction;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.function.Constant;
import org.apache.commons.math3.util.FastMath;

import ca.shapedetector.ShapeDetector;
import ca.shapedetector.distribution.AbsoluteGradient;
import ca.shapedetector.distribution.Distribution;
import ca.shapedetector.distribution.RadialDistance;
import ca.shapedetector.path.SDPath;

/**
 * A shape derived from the outlineCells of a blob found by CAShapeDetector.
 * 
 * @author Sean
 */
public abstract class AbstractShape implements SDShape {
	protected final static double DEFAULT_TOLERANCE = 0.05d; // 0.3d;

	protected final static Distribution RADIAL_DISTANCE = new RadialDistance();
	protected final static Distribution GRADIENT_DISTRIBUTION = new AbsoluteGradient();
	protected final static Distribution DEFAULT_DISTRIBUTION = RADIAL_DISTANCE;

	/** Path that defines this shape as a polygon. */
	protected final SDPath path;
	/** The method of collection distribution data. */
	protected final Distribution distributionType;
	// /** The shape distribution function. */
	// protected final UnivariateDifferentiableFunction distributionFunction;
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected final double tolerance;

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path that describes this shape.
	 * @param distribution
	 */
	public AbstractShape(final SDPath path,
			final Distribution distributionType, final double tolerance) {
		this.path = path;
		this.distributionType = distributionType;
		this.tolerance = tolerance;
		// if (path != null) {
		// distributionFunction = loadShapeDistribution();
		// } else {
		// distributionFunction = null;
		// }
	}

	/**
	 * Constructor.
	 * 
	 * @param distribution
	 */
	protected AbstractShape(final Distribution distribution,
			final double tolerance) {
		this(null, distribution, tolerance);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param shape
	 */
	public AbstractShape(AbstractShape shape) {
		path = new SDPath(shape.path);
		this.distributionType = shape.distributionType;
		this.tolerance = shape.tolerance;
		// distributionFunction = shape.distributionFunction;
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
	public UnivariateDifferentiableFunction getDistribution(
			final Distribution distributionType) {
		// return distributionFunction;

		double x0 = 0;
		double x1 = path.getPerimeter();
		if (x1 < 3.0) {
			return new Constant(0.0);
		}
		UnivariateFunction f = distributionType.compute(path);
		return new PeriodicDifferentiableFunction(f, x0, x1);
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

	public AbstractShape identify(final AbstractShape abstractShape) {
		AbstractShape shape = abstractShape;
		
		if (shape == null) {
			throw new RuntimeException();
		}
		/* For debugging */
		if (ShapeDetector.debug) {
			SDPanel.displayActiveShape(shape);
		}

		AbstractShape mask = getMask(shape);
		if (mask == null) {
			return null;
		}

		/* For debugging */
		if (ShapeDetector.debug) {
			SDPanel.displayMaskShape(shape, mask);

			double x0 = -1.0;
			double x1 = shape.getPath().getPerimeter();
			UnivariateDifferentiableFunction f1 = shape
					.getDistribution(distributionType);
			UnivariateDifferentiableFunction f2 = mask
					.getDistribution(distributionType);

			/* Displays a chart of the shape distribution. */
			if (ShapeDetector.debug) {
				graphics.LineChartFrame.frame.setTitle("Shape distribution");
				// graphics.LineChartFrame.displayData(x0, x1, f1, f2);
				UnivariateDifferentiableFunction g1 = new math.functions.Differential(
						f1);
				UnivariateDifferentiableFunction g2 = new math.functions.Differential(
						f2);
				graphics.LineChartFrame.displayData(x0, x1, g1, g2);
			}
		}

		double match = mask.compare(shape);
		/* Input.waitforSpace() */

		if (1.0 - match < tolerance) {
			shape = mask.identifySubclass();
		}
		return shape;
	}

	protected AbstractShape getMask(final AbstractShape shape) {
		return shape;
	}

	protected AbstractShape identifySubclass() {
		return this;
	}

	/**
	 * Calculates the difference ratio between this shape and the specified
	 * shape.
	 * 
	 * @param shape
	 *            The shape to compare this shape to.
	 * @return The difference ratio.
	 */
	public double compare(final AbstractShape shape) {
		return compareByAreaDifference(shape);
		// return compareByArea(shape);
		// return compareByPerimeter(shape);
		// return compareByDistribution(shape);
	}

	/**
	 * This method just is no good for matching any shapes.
	 * 
	 * @param shape
	 * @return
	 */
	protected double compareByPerimeter(final AbstractShape shape) {
		double a = shape.getPath().getPerimeter();
		double b = path.getPerimeter();
		return FastMath.min(a, b) / FastMath.max(a, b);
	}

	/**
	 * This method matches quadrilaterals, but also matches ellipses to
	 * quadrilaterals when they have the same area. So let's not use this...
	 * 
	 * @param shape
	 * @return
	 */
	protected double compareByArea(final AbstractShape shape) {
		double a = shape.getPath().getArea();
		double b = path.getArea();
		return FastMath.min(a, b) / FastMath.max(a, b);
	}

	/*
	 * This method also has its problems. Even very similar shapes may start
	 * from different points and have much different perimeter lengths etc which
	 * makes comparing their graphs difficult.
	 */
	protected double compareByDistribution(final AbstractShape shape) {
		/* The mask */
		UnivariateDifferentiableFunction f1 = getDistribution(distributionType);
		/* The target shape */
		UnivariateDifferentiableFunction f2 = shape
				.getDistribution(distributionType);

		SDPath path1 = getPath();
		SDPath path2 = shape.getPath();

		double x0 = 0;
		double x1 = path2.getPerimeter();
		if (x1 - x0 <= 0) {
			return 0.0;
		}
		/*
		 * Aligns the graphs by finding a common vertex, and comparing the
		 * distance from each starting point.
		 */
		Point2D start = path1.getVertices().get(0);
		double theta = path2.getOutlineMap().getDistance(start);
		// f2 = new PeriodicDifferentiableFunction(f2, x0, x1, -theta);
		UnivariateDifferentiableFunction f3 = new PeriodicDifferentiableFunction(
				f2, x0, x1, theta);

		UnivariateDifferentiableFunction f = new CyclicSimilarity(f1, f2, 1,
				x0, x1);

		/* Stretches/compresses the graphs to fit over the same domain. */
		// f1 = new StretchDifferentiableFunction(f1, x0, path1.getPerimeter(),
		// x0, x1);
		// f2 = new StretchDifferentiableFunction(f2, x0, path2.getPerimeter(),
		// x0, x1);

		// CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		// double similarity = f.value(criticalPoints.maximum());

		/* Gets the similarity between the graphs when both are already aligned. */
		double similarity = f.value(0d);

		if (ShapeDetector.debug) {
			System.out.println(similarity);
			graphics.LineChartFrame.frame.setTitle("Shape distribution");
			graphics.LineChartFrame.displayData(x0, x1, f1, f2, f3, f);
		}
		return similarity;
	}

	protected double compareByAreaDifference(final AbstractShape shape) {
		Area maskAreaPolygon = getPath().getAreaPolygon();
		Area shapePolygon = shape.getPath().getAreaPolygon();

		double maskArea = getPath().getArea();
		double shapeArea = shape.getPath().getArea();

		shapePolygon.add(maskAreaPolygon);
		SDPath totalAreaPath = new SDPath(shapePolygon);
		double totalArea = totalAreaPath.getArea();

		// Debug.displayMaskShape(shape, new UnknownShape(totalAreaPath));

		/* Path either contains curved segments or crosses itself */
		if (totalArea < shapeArea) {
			maskArea -= shapeArea - totalArea;
			totalArea = shapeArea;
		}

		return (shapeArea + maskArea - totalArea) / totalArea;
	}

}