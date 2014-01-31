package ca.shapedetector.shapes;

import exceptions.NullParameterException;
import graphics.SDPanel;
import graphics.SDPanelTheme;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import math.functions.CyclicSimilarity;
import math.functions.Differential;
import math.functions.PeriodicDifferentiableFunction;
import math.functions.StretchFunction;

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

	public AbstractShape identify(final AbstractShape abstractShape)
			throws NullParameterException {
		if (abstractShape == null) {
			throw new NullParameterException("abstractShape");
		}

		AbstractShape shape = abstractShape;

		AbstractShape mask = getMask(shape);
		if (mask == null) {
			return null;
		}

		/* For debugging */
		if (ShapeDetector.debug) {
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
				UnivariateFunction g1 = new StretchFunction(f1, x0, x1, x0, x1);
				UnivariateFunction g2 = new StretchFunction(f2, x0, mask
						.getPath().getPerimeter(), x0, x1);

				UnivariateFunction dg1 = new Differential(g1, 1);

				graphics.LineChartFrame.displayData(x0, x1, g1, g2, dg1);
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

	static JFrame frame1 = new JFrame();
	static JFrame frame2 = new JFrame();
	static JFrame frame3 = new JFrame();

	protected double compareByAreaDifference(final AbstractShape shape) {
		Area maskAreaPolygon = getPath().getAreaPolygon();
		Area shapePolygon = shape.getPath().getAreaPolygon();
		//
		double maskArea = getPath().getArea();
		double shapeArea = shape.getPath().getArea();
		//
//		if (!shapePolygon.isPolygonal() || !maskAreaPolygon.isPolygonal()
//				|| !shape.getPath().getAreaPolygon().isSingular()
//				|| !maskAreaPolygon.isSingular()) {
//
//			PathIterator iterator = shapePolygon.getPathIterator(null);
//			System.out.println("***");
//			while (!iterator.isDone()) {
//				double[] coords = new double[6];
//				int status = iterator.currentSegment(coords);
//				System.out.println("{" + coords[0] + "," + coords[1] + "},");
//				if (status == 4) {
//					System.out.println("END");
//				}
//				iterator.next();
//			}
//
//			System.out.println("ERROR");
//		}
		shapePolygon.add(maskAreaPolygon);
		SDPath totalAreaPath = new SDPath(shapePolygon);
		double totalArea = totalAreaPath.getArea();

		if (ShapeDetector.debug) {
			AbstractShape bgShape = new UnknownShape(totalAreaPath);
			 SDPanel.displayShape(bgShape, SDPanelTheme.BG);
			SDPanel.displayShape(shape, SDPanelTheme.DEFAULT);
			SDPanel.displayShape(shape, this, SDPanelTheme.MASK);

			// SDPanel.displayShape(bgShape, bgShape, SDPanelTheme.HIGHLIGHT);

			// display(bgShape, frame1);
			// display(shape, frame2);
			// display(this, frame3);

			frame1.setTitle("Combined");
			frame2.setTitle("Shape");
			frame3.setTitle("Mask");
		}

		/* Path either contains curved segments or crosses itself. */
		if (totalArea < shapeArea) {
			maskArea -= shapeArea - totalArea;
			totalArea = shapeArea;
		}

//		shapeClasses.add(new Triangle())
		double result = (shapeArea + maskArea - totalArea) / totalArea;
		/* Should make the comparison more forgiving for smaller shapes. */
		return result;
	}

	protected void display(AbstractShape shape, JFrame frame) {
		Rectangle2D bounds = shape.getPath().getBounds();
		SDPanel panel = new SDPanel();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
		panel.display(shape);

		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}

}