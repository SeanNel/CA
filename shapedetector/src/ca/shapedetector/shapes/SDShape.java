package ca.shapedetector.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

import ca.shapedetector.path.SDPath;

import std.Picture;

/**
 * A shape derived from the outlineCells of a ProtoShape found by
 * CAShapeDetector.
 * 
 * @author Sean
 */
public class SDShape {
	/** Path that defines this shape as a polygon. */
	protected SDPath path;
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

	/**
	 * Singleton constructor.
	 */
	public SDShape() {
		defaultColours();
	}

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path that describe this shape.
	 * @param graphics
	 *            The graphics object to draw to.
	 */
	public SDShape(SDPath path, Picture picture) {
		this.path = path;
		this.picture = picture;
		defaultColours();
	}

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

	protected void loadRelatedShapes() {
		/* Method stub */
	}

	protected void defaultColours() {
		fillColour = new Color(230, 245, 230, 100);
		outlineColour = Color.red; // Color.green;
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
	 * Gets this CAShape's center.
	 * 
	 * @return The centroid's coordinates.
	 */
	public double[] getCenter() {
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
		return path.getCentroid();
	}

	/**
	 * Gets the area enclosed by this shape, specifically, the area enclosed by
	 * this polygon.
	 * 
	 * @return
	 */
	public double getArea() {
		return path.getArea();
	}

	/**
	 * Gets the angle of orientation of this shape, relative to the x-axis, in
	 * radians.
	 * 
	 * @return
	 */
	public double getOrientation() {
		return path.getOrientation();
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
		int centroidX = (int) getCentroid()[0];
		int centroidY = (int) getCentroid()[1];

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
		int centroidX = (int) getCentroid()[0];
		int centroidY = (int) getCentroid()[1];

		drawString(getClass().getSimpleName(), centroidX, centroidY - 10);
		// drawString(getDescription(), centroidX, centroidY + 10);
		drawString("x=" + centroidX + ", y=" + centroidY,
				centroidX, centroidY + 10);
		int theta = (int) Math.toDegrees(path.getOrientation());
		drawString("theta=" + theta,
				centroidX, centroidY + 30);
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
	public SDShape identifyShape(SDPath path) {
		SDShape identifiedShape = null;

		for (SDShape relatedShape : relatedShapes) {
			identifiedShape = relatedShape.identify(path);

			if (identifiedShape != null) {
				identifiedShape.picture = picture;
				return identifiedShape;
			}
		}
		return new SDUnknownShape(path, picture);
	}

	/**
	 * Returns an instance of the shape described by this polygon.
	 * <p>
	 * Subclasses should extend this.
	 * 
	 * @return An instance of the detected shape if detected or null otherwise.
	 */
	protected SDShape identify(SDPath path) {
		return null;
	}

	/**
	 * For debugging.
	 */
	public void display() {
		path.displayHighlight(picture);
	}
}