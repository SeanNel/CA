package ca.shapedetector.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.util.ArrayList;
import java.util.List;

import std.Picture;

import ca.shapedetector.CAProtoShape;

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
	protected Graphics2D graphics;
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
	 * Singleton constructor.
	 */
	public SDShape(Graphics2D graphics) {
		this.graphics = graphics;
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
	public SDShape(SDPath path, Graphics2D graphics) {
		this.path = path;
		this.graphics = graphics;
		defaultColours();
	}

	/**
	 * Master constructor.
	 * 
	 * @param canvas
	 *            A picture to render identified shapes on.
	 */
	public SDShape(Picture picture) {
		graphics = picture.getImage().createGraphics();
		defaultColours();
		relatedShapes = new ArrayList<SDShape>();
		relatedShapes.add(new SDRectangle(graphics));
		relatedShapes.add(new SDEllipse(graphics));
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
	public Rectangle getBoundaries() {
		return path.getBounds();
	}

	/**
	 * Gets the dimensions of this CAShape, for example its width and height.
	 * 
	 * @return The dimensions of this CAShape.
	 */
	public int[] getDimensions() {
		Rectangle bounds = path.getBounds();

		int[] dimensions = { bounds.width, bounds.height };
		return dimensions;
	}

	/**
	 * Gets this CAShape's centroid.
	 * 
	 * @return The centroid's coordinates.
	 */
	public double[] getCentroid() {
		Rectangle bounds = path.getBounds();

		double[] centroid = { bounds.getCenterX(), bounds.getCenterY() };
		return centroid;
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
		int[] dimensions = getDimensions();
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
		path.draw(graphics, outlineColour, fillColour);
	}

	/**
	 * Draws a cross at the centroid of the shape in the specified colour.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawCentroid() {
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
		graphics.setColor(labelColour);
		int centroidX = (int) getCentroid()[0];
		int centroidY = (int) getCentroid()[1];

		drawString(getClass().getSimpleName(), centroidX, centroidY - 10);
		// drawString(getDescription(), centroidX, centroidY + 10);
		drawString("x=" + centroidX + ", y=" + centroidY, centroidX,
				centroidY + 10);
	}

	/**
	 * Draws a string.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawString(String string, int x, int y) {
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
	 * @param protoShape
	 *            An unidentified shape.
	 * @return An instance of the detected shape.
	 */
	public SDShape identifyShape(CAProtoShape protoShape) {
		SDPath path = new SDPath(protoShape);

		SDShape identifiedShape = null;
		for (SDShape relatedShape : relatedShapes) {
			identifiedShape = relatedShape.identify(path);

			if (identifiedShape != null) {
				identifiedShape.graphics = graphics;
				return identifiedShape;
			}
		}
		return new SDUnknownShape(path, graphics);
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
}