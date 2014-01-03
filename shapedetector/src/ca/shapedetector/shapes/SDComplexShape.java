package ca.shapedetector.shapes;

import graphics.LineChart;

import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

import math.CartesianVector;
import math.DiscreteFunction;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYIntervalSeriesCollection;

import std.Picture;

import ca.shapedetector.path.SDDistributionHistogram;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.path.SDPathIterator;

/**
 * An arbitrary shape. Needs more work.
 * 
 * @author Sean
 */
public class SDComplexShape extends SDShape {
	/**
	 * The distribution comparison type. Different techniques may be better at
	 * detecting different shapes.
	 */
	protected int distributionType;

	/* Keeps a chart handy to display distribution data. */
	protected static final XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
	public static final LineChart distributionChart = new LineChart(dataset);

	public static boolean showDistributionGraphs = false;

	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.12;

	/**
	 * The counter-clockwise angle between the reference axis of this shape and
	 * the x-axis, in radians.
	 */
	protected double orientation;
	private double length;
	private double width;

	public SDComplexShape(Picture picture) {
		super(picture);

		// distributionType =
		// SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION;
		loadChart();
	}

	public SDComplexShape(SDPath path, Picture picture) {
		super(path, picture);
		getProperties();
	}

	public SDComplexShape(SDShape shape) {
		super(shape);
	}

	public SDComplexShape(SDComplexShape shape) {
		super(shape);

		distributionType = shape.distributionType;
	}

	protected void loadRelatedShapes() {
	}

	protected void loadChart() {
		distributionChart.chart.setTitle("Shape distribution");

		ValueAxis xAxis = distributionChart.chart.getXYPlot().getDomainAxis();
		ValueAxis yAxis = distributionChart.chart.getXYPlot().getRangeAxis();

		switch (distributionType) {
		case SDDistributionHistogram.RADIAL_DISTANCE_DISTRIBUTION:
			yAxis.setLabel("Distance (px)");
			break;
		case SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION:
			yAxis.setLabel("Gradient (rad)");
			break;
		case SDDistributionHistogram.ABSOLUTE_GRADIENT_DISTRIBUTION:
			yAxis.setLabel("Absolute gradient (rad)");
			break;
		case SDDistributionHistogram.RADIAL_AREA_DISTRIBUTION:
			xAxis.setLabel("Azimuth (deg)");
			yAxis.setLabel("Area (px^2)");
			break;
		}
	}

	public double compare(SDShape shape) {
		// super(shape);
		/* Begin by aligning the distribution graphs. */
		double g[] = getAlignedDistribution(shape);

		double[] distributionData = SDDistributionHistogram
				.getGradientDistribution(shape, distributionType);
		double correlation = DiscreteFunction.getCorrelation(distributionData,
				g);

		// System.out.println(correlation);
		if (showDistributionGraphs) {
			dataset.removeAllSeries();
			dataset.addSeries(distributionChart.getSeries(distributionData,
					"Identity shape"));
			dataset.addSeries(distributionChart.getSeries(g,
					"Unidentified shape"));
			if (!distributionChart.isFocusableWindow()) {
				distributionChart.setVisible(true);
			}
		}
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
		double[] ref = SDDistributionHistogram.getGradientDistribution(this,
				distributionType);
		double[] f = SDDistributionHistogram.getGradientDistribution(shape,
				distributionType);

		// int t = distributionData.length;
		// f = DiscreteFunction.bandPass(f, t - 1, t);

		f = DiscreteFunction.crossCorrelation(ref, f);

		double x = DiscreteFunction.findPeak(f);
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
		double[] g = SDDistributionHistogram.getGradientDistribution(shape,
				SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION);
		double theta = orientation - calculateOrientation(shape);
		int x = (int) Math.round(theta * g.length / Math.PI / 2.0);
		DiscreteFunction.rotate(g, x);
		return g;
	}

	protected SDShape identify(SDShape shape) {
		SDShape identity = getIdentity(shape);
		double match = identity.compare(shape);

		if (1.0 - match < tolerance) {
			SDComplexShape complexShape = new SDComplexShape(this);
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
		SDShape identity = new SDComplexShape(path, picture);
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
