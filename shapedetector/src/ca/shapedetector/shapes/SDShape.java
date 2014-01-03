package ca.shapedetector.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import std.Picture;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;
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

	/** Draws shapes as they are identified. For debugging. */
	public static boolean showActiveShape = false;
	public static boolean showDetectedShape = true;

	/**
	 * Master constructor.
	 * 
	 * @param canvas
	 *            A picture to render identified shapes on.
	 */
	public SDShape(Picture picture) {
		this.picture = picture;
		defaultColours();
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
		path = shape.path;
		picture = shape.picture;
		// relatedShapes...

		centroid = shape.centroid;
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

	/**
	 * Gets the boundary coordinates of this shape.
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
	 * */
	protected void drawShape() {
		path.draw(picture, outlineColour, fillColour);
	}

	/**
	 * Draws a cross at the centroid of the shape in the specified colour.
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
	 */
	public void drawLabel() {
		int centroidX = (int) centroid[0];
		int centroidY = (int) centroid[1];

		drawString(getClass().getSimpleName(), centroidX, centroidY - 10);
		// drawString(getDescription(), centroidX, centroidY + 10);
		drawString("x=" + centroidX + ", y=" + centroidY, centroidX,
				centroidY + 10);
	}

	/**
	 * Draws a string.
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
			/*
			 * With this on it can be seen there is some oddly shaped thing
			 * being picked up...
			 */
			shape.outlineColour = Color.green;
			shape.drawShape();
			ca.draw();
		}

		for (SDShape relatedShape : relatedShapes) {
			identifiedShape = relatedShape.identify(shape);
			// Input.waitForSpace();

			if (identifiedShape != null) {
				identifiedShape.picture = picture;

				if (showDetectedShape) {
					// shape.outlineColour = Color.blue;
					identifiedShape.drawLabel();
					ca.draw();
				}
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
	 * to calculate the areas of sectors.
	 * 
	 * @return
	 */
	public Area getAreaPolygon() {
		return path.getAreaPolygon();
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