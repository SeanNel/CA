package ca.shapedetector.shapes;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;

import ca.shapedetector.path.SDPath;
import ca.shapedetector.path.SDPathIterator;

/**
 * A shape derived from the outlineCells of a blob found by CAShapeDetector.
 * 
 * @author Sean
 */
public class SDShape implements Iterable<double[]> {
	/** Path that defines this shape as a polygon. */
	protected SDPath path;
	protected double[] centroid;
	protected double area;

	/** A list of shapes that this shape is a supertype of. */
	protected List<SDShape> relatedShapes;

	/** Draws shapes as they are identified. For debugging. */
	public static boolean showActiveShape = false;
	public static boolean showDetectedShape = true;

	/* For debugging. */
	public static final JFrame displayFrame = new JFrame();

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
		path = shape.path;
		// relatedShapes...

		centroid = shape.centroid;
		area = shape.area;
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
	}

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
	 */
	protected SDShape identify(SDShape shape) {
		return null;
	}

	/**
	 * Calculates the difference ratio between this shape and the specified
	 * shape.
	 * <p>
	 * Subclasses should extend this method.
	 * 
	 * @param shape
	 *            The shape to compare this shape to.
	 * @return The difference ratio.
	 */
	public double compare(SDShape shape) {
		/* Method stub. */
		return 0.0;
	}

	/**
	 * Gets an Area object based on this shape. Used by SDDistributionHistogram
	 * to calculate the areas of sectors and for graphics display.
	 * 
	 * @return
	 */
	public Area getAreaPolygon() {
		return path.getAreaPolygon();
	}

	public SDPath getPath() {
		return path;
	}

	/**
	 * Iterates along points on the outline of this shape.
	 */
	@Override
	public SDPathIterator iterator() {
		return path.iterator();
	}

	public double[][] getOutline() {
		return path.getOutline();
	}
}