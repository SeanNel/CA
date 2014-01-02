package ca.shapedetector.shapes;

import graphics.LineChart;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import math.CartesianVector;
import math.DiscreteFunction;

import org.jfree.chart.axis.ValueAxis;
import org.jfree.data.xy.XYIntervalSeriesCollection;

import std.Picture;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.path.SDDistributionHistogram;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.path.SDPathIterator;

/**
 * A shape derived from the outlineCells of a ProtoShape found by
 * CAShapeDetector.
 * 
 * @author Sean
 */
public class SDShape implements Iterable<double[]> {
	/** Path that defines this shape as a polygon. */
	protected SDPath path;
	protected double[] centroid;
	protected double area;
	/**
	 * The counter-clockwise angle between the reference axis of this shape and
	 * the x-axis, in radians.
	 */
	protected double orientation;
	/**
	 * The distribution comparison type. Different techniques may be better at
	 * detecting different shapes.
	 */
	protected int distributionType;
	/**
	 * Distribution data. Could be the area, gradient or distance from centroid
	 * distribution.
	 */
	protected double[] distributionData;

	/** A list of shapes that this shape is a supertype of. */
	protected List<SDShape> relatedShapes;
	/** Picture to draw on. */
	protected Picture picture;
	/** Shape's fill colour. */
	protected Color fillColour;
	/** Shape's outline colour. */
	protected Color outlineColour;
	/** Shape's centroid colour. */
	protected Color centroidColour;
	/** Shape's text label colour. */
	protected Color labelColour;
	protected static final Font DEFAULT_FONT = new Font("SansSerif",
			Font.PLAIN, 10);
	/** Shape's text label font. */
	protected Font font;

	/* Keeps a chart handy to display distribution data. */
	protected static final XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
	public static final LineChart distributionChart = new LineChart(dataset);

	public static boolean showDistributionGraphs = false;
	public static boolean showActiveShape = false;

	/**
	 * Master constructor.
	 * 
	 * @param canvas
	 *            A picture to render identified shapes on.
	 */
	public SDShape(Picture picture) {
		distributionType = SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION; // GRADIENT_DISTRIBUTION;

		this.picture = picture;
		defaultColours();
		relatedShapes = new ArrayList<SDShape>();
		loadRelatedShapes();
		loadChart();
	}

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path that describes this shape.
	 * @param graphics
	 *            The graphics object to draw to.
	 */
	public SDShape(SDPath path, Picture picture) {
		this.picture = picture;
		defaultColours();

		loadPath(path);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param shape
	 */
	public SDShape(SDShape shape) {
		this.path = shape.path;
		picture = shape.picture;
		// relatedShapes...

		distributionType = shape.distributionType;
		distributionData = shape.distributionData;
		centroid = shape.centroid;
		orientation = shape.orientation;
		area = shape.area;

		fillColour = shape.fillColour;
		outlineColour = shape.outlineColour;
		centroidColour = shape.centroidColour;
		labelColour = shape.labelColour;
		font = shape.font;
	}

	protected void loadPath(SDPath path) {
		this.path = path;
		/*
		 * Cannot derive the centroid from the areaCells when shapes contain
		 * nested shapes. So for now, the centre of gravity is the same as the
		 * geometric centre. This only becomes a problem once we test for
		 * non-symmetrical shapes.
		 */
		centroid = new double[2];
		centroid[0] = path.getCentreX();
		centroid[1] = path.getCentreY();

		loadDistributionData();
	}

	protected void loadRelatedShapes() {
		/* Method stub */
	}

	protected void defaultColours() {
		fillColour = new Color(230, 245, 230, 100);
		outlineColour = Color.red;
		centroidColour = Color.magenta;
		labelColour = Color.blue;
		font = DEFAULT_FONT;
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

	/**
	 * Gets the boundary coordinates of this shape.
	 * 
	 * @return A row for each axis, e.g. x and y, with columns for minima and
	 *         maxima.
	 */
	public Rectangle2D getBoundaries() {
		return path.getBounds();
	}

	/**
	 * Gets the dimensions of this CAShape, for example its width and height.
	 * 
	 * @return The dimensions of this CAShape.
	 */
	public double[] getDimensions() {
		Rectangle2D bounds = path.getBounds();

		double[] dimensions = { bounds.getWidth(), bounds.getHeight() };
		return dimensions;
	}

	/**
	 * Gets this CAShape's centre.
	 * 
	 * @return The centroid's coordinates.
	 */
	public double[] getCentre() {
		Rectangle2D bounds = path.getBounds();

		double[] center = { bounds.getCenterX(), bounds.getCenterY() };
		return center;
	}

	/**
	 * Gets this CAShape's centroid (center of gravity).
	 * 
	 * @return The centroid's coordinates.
	 */
	public double[] getCentroid() {
		return centroid;
	}

	/**
	 * Gets the area enclosed by this shape, specifically, the area enclosed by
	 * this polygon.
	 * 
	 * @return
	 */
	public double getArea() {
		return area;
	}

	/**
	 * Gets the angle of orientation of this shape, relative to the x-axis, in
	 * radians.
	 * 
	 * @return
	 */
	public double getOrientation() {
		return orientation;
	}

	public String toString() {
		return "(" + this.getClass().getSimpleName() + ") [" + getDescription()
				+ ", centroid: " + arrayToString(getCentroid())
				+ ", dimensions: " + arrayToString(getDimensions())
				+ ", area: " + getArea() + ", orientation: " + getOrientation()
				+ "]";
	}

	protected String getDescription() {
		double[] dimensions = getDimensions();
		return "w=" + dimensions[0] + ", h=" + dimensions[1];
	}

	protected String arrayToString(int[] array) {
		if (array == null || array.length == 0) {
			return "";
		}

		String str = "" + array[0];
		for (int i = 1; i < array.length; i++) {
			str += ", " + array[i];
		}
		return str;
	}

	protected String arrayToString(double[] array) {
		if (array == null || array.length == 0) {
			return "";
		}

		String str = "" + array[0];
		for (int i = 1; i < array.length; i++) {
			str += ", " + array[i];
		}
		return str;
	}

	/**
	 * Draws this shape and additional information to the canvas.
	 */
	public void draw() {
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		drawShape();
		drawCentroid();
		drawLabel();
	}

	/**
	 * Draws this shape.
	 * 
	 * @param colour
	 * */
	protected void drawShape() {
		path.draw(picture, outlineColour, fillColour);
	}

	/**
	 * Draws a cross at the centroid of the shape in the specified colour.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawCentroid() {
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(centroidColour);
		int centroidX = (int) centroid[0];
		int centroidY = (int) centroid[1];

		graphics.drawLine(centroidX - 2, centroidY - 2, centroidX + 2,
				centroidY + 2);
		graphics.drawLine(centroidX - 2, centroidY + 2, centroidX + 2,
				centroidY - 2);
	}

	/**
	 * Draws a descriptive label of the shape.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawLabel() {
		int centroidX = (int) centroid[0];
		int centroidY = (int) centroid[1];

		drawString(getClass().getSimpleName(), centroidX, centroidY - 10);
		// drawString(getDescription(), centroidX, centroidY + 10);
		drawString("x=" + centroidX + ", y=" + centroidY, centroidX,
				centroidY + 10);
		int theta = (int) Math.toDegrees(orientation);
		drawString("theta=" + theta, centroidX, centroidY + 30);
	}

	/**
	 * Draws a string.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawString(String string, int x, int y) {
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(labelColour);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();

		int ws = metrics.stringWidth(string);
		int hs = metrics.getDescent();

		graphics.drawString(string, (int) (x - ws / 2.0), (float) (y + hs));
	}

	/**
	 * Identifies the shape.
	 * <p>
	 * Assumes that the ProtoShape's outline cells have already been arranged in
	 * sequence.
	 * 
	 * @param path
	 *            A path describing the unidentified shape.
	 * @return An instance of the detected shape.
	 */
	public SDShape identifyShape(CAProtoShape protoShape, CAShapeDetector ca) {
		SDShape identifiedShape = null;

		SDShape shape = new SDShape(new SDPath(protoShape), picture);

		if (showActiveShape) {
			shape.draw();
			ca.draw();
		}

		for (SDShape relatedShape : relatedShapes) {
			identifiedShape = relatedShape.identify(shape);
			// Input.waitForSpace();

			if (identifiedShape != null) {
				identifiedShape.picture = picture;
				return identifiedShape;
			}
		}
		return new SDUnknownShape(shape);
	}

	/**
	 * Returns an instance of the shape described by this polygon.
	 * <p>
	 * Subclasses should extend this.
	 * 
	 * @return An instance of the detected shape if detected or null otherwise.
	 */
	protected SDShape identify(SDShape shape) {
		return null;
	}

	protected void loadDistributionData() {
		double min;
		double max;

		distributionData = SDDistributionHistogram.getGradientDistribution(
				this, distributionType);

		switch (distributionType) {
		case SDDistributionHistogram.RADIAL_AREA_DISTRIBUTION:
		case SDDistributionHistogram.RADIAL_DISTANCE_DISTRIBUTION:
			double f[] = distributionData.clone();
			DiscreteFunction.absoluteValue(f);

			min = DiscreteFunction.minimum(f);
			max = DiscreteFunction.maximum(f);
			break;
		case SDDistributionHistogram.ABSOLUTE_GRADIENT_DISTRIBUTION:
		case SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION:
			DiscreteFunction.medianFilter(distributionData, 5);

			min = 0.0;
			max = Math.PI;
			break;
		default:
			min = 0.0;
			max = 1.0;
		}

		distributionData = DiscreteFunction.fit(distributionData, 100);

		/* Normalizes the data. */
		DiscreteFunction.add(distributionData, -min);
		DiscreteFunction.times(distributionData, 1.0 / (max - min));
	}

	/**
	 * Gets the orientation of this shape using the specified shape as
	 * reference.
	 * 
	 * @param shape
	 * @return
	 */
	protected double calculateOrientation(SDShape shape) {
		double orientation = shape.getStartingOrientation();
		double[] ref = distributionData;
		double[] f = shape.distributionData;

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
	private double getStartingOrientation() {
		SDPathIterator iterator = path.iterator();
		double[] first = iterator.next();
		double[] v1 = { first[0] - centroid[0], first[1] - centroid[1] };
		/* 360 - angle = anticlockwise angle from x-axis. */
		return 2.0 * Math.PI - CartesianVector.getAngle(v1);
	}

	/**
	 * Calculates the difference ratio between this shape and the specified
	 * shape.
	 * <p>
	 * This method should be redefined to detect specific shapes.
	 * 
	 * @param shape
	 *            The shape to compare this shape to.
	 * @return The difference ratio.
	 */
	public double compare(SDShape shape) {
		/* Begin by aligning the distribution graphs. */
		double g[] = getAlignedDistribution(shape);

		double correlation = DiscreteFunction.getCorrelation(distributionData,
				g);

//		System.out.println(correlation);
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

	/* Assumes current shape is at an orientation of 0 */
	protected double[] getAlignedDistribution(SDShape shape) {
		double[] g = shape.distributionData;
		double theta = orientation - calculateOrientation(shape);
		int x = (int) Math.round(theta * g.length / Math.PI / 2.0);
		DiscreteFunction.rotate(g, x);
		return g;
	}

	public SDPath getPath() {
		return path;
	}

	@Override
	public SDPathIterator iterator() {
		return path.iterator();
	}
}