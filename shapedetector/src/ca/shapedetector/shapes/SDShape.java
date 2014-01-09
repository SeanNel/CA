package ca.shapedetector.shapes;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import math.discrete.DiscreteFunction;
import math.discrete.dbl.DiscreteFunctionDoublePeriodic;

import ca.concurrency.Updatable;
import ca.shapedetector.Distribution;
import ca.shapedetector.ShapeDetector;
import ca.shapedetector.path.SDPath;
import exceptions.MethodNotImplementedException;

/**
 * A shape derived from the outlineCells of a blob found by CAShapeDetector.
 * 
 * @author Sean
 */
public class SDShape implements Updatable {
	/** Path that defines this shape as a polygon. */
	protected SDPath path;
	protected double[] centroid;
	protected double area;

	/** A list of shapes that this shape is a supertype of. */
	protected List<SDShape> relatedShapes;
	protected int comparisonType;

	/**
	 * Master constructor.
	 * 
	 * @param canvas
	 *            A picture to render identified shapes on.
	 */
	public SDShape() {
		relatedShapes = new ArrayList<SDShape>();
		loadRelatedShapes();
	}

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path that describes this shape.
	 * @param graphics
	 *            The graphics object to draw to.
	 */
	public SDShape(SDPath path) {
		loadPath(path);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param shape
	 */
	public SDShape(SDShape shape) {
		path = new SDPath(shape.path);
		// relatedShapes...

		centroid = shape.centroid.clone();
		area = shape.area;
	}

	protected void loadPath(SDPath path) {
		this.path = path;
		/*
		 * Cannot derive the centroid from the areaCells when shapes contain
		 * nested shapes. So for now, the centre of gravity is the same as the
		 * geometric centre. This only becomes an issue once we test for
		 * non-symmetrical shapes.
		 */
		centroid = new double[2];
		centroid[0] = path.getCentreX();
		centroid[1] = path.getCentreY();
	}

	/**
	 * Loads the list of related shapes.
	 */
	protected void loadRelatedShapes() {
		/* Method stub */
	}

	/**
	 * Gets the bounding rectangle of this shape.
	 * 
	 * @return A row for each axis, e.g. x and y, with columns for minima and
	 *         maxima.
	 */
	public Rectangle2D getBounds() {
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
	 * Gets this CAShape's centroid (center of gravity). This is currently the
	 * same as the geometric centre.
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

	public String toString() {
		return "(" + this.getClass().getSimpleName() + ") [" + getDescription()
				+ ", centroid: " + arrayToString(getCentroid())
				+ ", dimensions: " + arrayToString(getDimensions())
				+ ", area: " + getArea() + "]";
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
	 * Returns an instance of the shape described by this polygon.
	 * <p>
	 * Subclasses should extend this.
	 * 
	 * @return An instance of the detected shape if detected or null otherwise.
	 * @throws MethodNotImplementedException
	 */
	protected SDShape identify(SDShape shape)
			throws MethodNotImplementedException {
		throw new MethodNotImplementedException();
	}

	/**
	 * Calculates the difference ratio between this shape and the specified
	 * shape.
	 * 
	 * @param shape
	 *            The shape to compare this shape to.
	 * @return The difference ratio.
	 */
	public double compare(SDShape shape) {
		DiscreteFunctionDoublePeriodic f1 = Distribution
				.getGradientDistribution(this, comparisonType);
		DiscreteFunctionDoublePeriodic f2 = Distribution
				.getGradientDistribution(shape, comparisonType);

		f1.resize(100, DiscreteFunction.RESIZE_STRETCH);
		f2.resize(100, DiscreteFunction.RESIZE_STRETCH);

		DiscreteFunctionDoublePeriodic g = f1.crossCorrelation(f2);
		/* Assumes the max correlation is a relative measure. */
		// double correlation = g.maximum();

		/*
		 * Assumes the max correlation is an absolute measure, so it finds a
		 * relative measure.
		 */
		int peak = g.maximumX();
		f2.rotate(peak);
		double correlation = f1.correlation(f2);

		if (ShapeDetector.debug) {
			System.out.println(correlation);
			graphics.LineChartFrame.frame.setTitle("Correlation");
			graphics.LineChartFrame.displayData(f1, f2, g);
		}

		return correlation;
	}

	/**
	 * Gets the path that describes this shape's outline.
	 * 
	 * @return
	 */
	public SDPath getPath() {
		return path;
	}

	public void move(double x, double y) {
		path.move(x, y);
		centroid[0] = x;
		centroid[1] = y;
	}
}