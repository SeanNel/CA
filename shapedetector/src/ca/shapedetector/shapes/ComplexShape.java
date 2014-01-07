package ca.shapedetector.shapes;


import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import math.discrete.Correlation;
import math.discrete.DiscreteFunction;
import math.discrete.Stats;
import math.vector.CartesianVector;

import ca.shapedetector.Distribution;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.path.SDPathIterator;
import exceptions.MethodNotImplementedException;

/**
 * An arbitrary shape. Needs more work.
 * 
 * @author Sean
 */
public class ComplexShape extends SDShape {
	/**
	 * The distribution comparison type. Different techniques may be better at
	 * detecting different shapes.
	 */
	protected int distributionType;

	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.12;

	/**
	 * The counter-clockwise angle between the reference axis of this shape and
	 * the x-axis, in radians.
	 */
	protected double orientation;
	private double length;
	private double width;

	public ComplexShape() {
		super();

		// distributionType =
		// SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION;
		// loadChart();
	}

	public ComplexShape(SDPath path) {
		super(path);
		getProperties();
	}

	public ComplexShape(SDShape shape) {
		super(shape);
	}

	public ComplexShape(ComplexShape shape) {
		super(shape);

		distributionType = shape.distributionType;
	}

	protected void loadRelatedShapes() {
	}

	// protected void loadChart() {
	// distributionChart.chart.setTitle("Shape distribution");
	//
	// ValueAxis xAxis = distributionChart.chart.getXYPlot().getDomainAxis();
	// ValueAxis yAxis = distributionChart.chart.getXYPlot().getRangeAxis();
	//
	// switch (distributionType) {
	// case SDDistribution.RADIAL_DISTANCE_DISTRIBUTION:
	// yAxis.setLabel("Distance (px)");
	// break;
	// case SDDistribution.RADIAL_GRADIENT_DISTRIBUTION:
	// yAxis.setLabel("Gradient (rad)");
	// break;
	// case SDDistribution.ABSOLUTE_GRADIENT_DISTRIBUTION:
	// yAxis.setLabel("Absolute gradient (rad)");
	// break;
	// case SDDistribution.RADIAL_AREA_DISTRIBUTION:
	// xAxis.setLabel("Azimuth (deg)");
	// yAxis.setLabel("Area (px^2)");
	// break;
	// }
	// }

	public double compare(SDShape shape) {
		// super(shape);
		/* Begin by aligning the distribution graphs. */
		double g[] = getAlignedDistribution(shape);

		double[] distributionData = Distribution.getGradientDistribution(
				shape, distributionType);
		double correlation = Correlation.getCorrelation(distributionData, g);

		return correlation;
	}

	/**
	 * Gets the orientation of this shape using the specified shape as
	 * reference.
	 * 
	 * @param shape
	 * @return
	 */
	protected double calculateOrientation(SDShape shape) {
		double orientation = getStartingOrientation(shape);
		double[] ref = Distribution.getGradientDistribution(this,
				distributionType);
		double[] f = Distribution.getGradientDistribution(shape,
				distributionType);

		// int t = distributionData.length;
		// f = DiscreteFunction.bandPass(f, t - 1, t);

		f = Correlation.crossCorrelation(ref, f);

		double x = Stats.findPeak(f);
		/*
		 * Makes the somewhat dodgy assumption that x-values on the graph
		 * correspond directly to angles...
		 */
		x = Math.toRadians(x / f.length * 360.0);
		orientation += x; // +Math.PI / 2.0
		if (orientation > 2.0 * Math.PI) {
			orientation -= 2.0 * Math.PI;
		}
		return orientation;
	}

	/** Gets the orientation of the starting point. */
	protected static double getStartingOrientation(SDShape shape) {
		SDPathIterator iterator = shape.path.iterator();
		double[] first = iterator.next();
		double[] v1 = { first[0] - shape.centroid[0],
				first[1] - shape.centroid[1] };
		/* 360 - angle = anticlockwise angle from x-axis. */
		return 2.0 * Math.PI - CartesianVector.getAngle(v1);
	}

	/* Assumes current shape is at an orientation of 0 */
	protected double[] getAlignedDistribution(SDShape shape) {
		double[] g = Distribution.getGradientDistribution(shape,
				Distribution.RADIAL_GRADIENT_DISTRIBUTION);
		double theta = orientation - calculateOrientation(shape);
		int x = (int) Math.round(theta * g.length / Math.PI / 2.0);
		DiscreteFunction.rotate(g, x);
		return g;
	}

	protected SDShape identify(SDShape shape) throws MethodNotImplementedException {
		SDShape identity = getIdentity(shape);
		double match = identity.compare(shape);

		if (1.0 - match < tolerance) {
			ComplexShape complexShape = new ComplexShape(this);
			return complexShape;
		} else {
			return null;
		}
	}

	protected SDShape getIdentity(SDShape shape) {
		// double[] distributionData = SDDistributionHistogram
		// .getGradientDistribution(shape,
		// SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION);

		SDPath path = new SDPath(new Path2D.Double());
		SDShape identity = new ComplexShape(path);
		return identity;
	}

	protected void getProperties() {
		Rectangle2D rectangle = getBounds();

		length = rectangle.getWidth();
		width = rectangle.getHeight();
	}

	protected String getDescription() {
		return "l=" + length + ", w=" + width;
	}

	public double getLength() {
		return length;
	}

	public double getWidth() {
		return width;
	}
}
