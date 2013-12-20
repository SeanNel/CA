package ca.shapedetector.shapes;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;

import std.Picture;

import ca.CACell;
import ca.shapedetector.CAProtoShape;

/**
 * A shape made up of CACells.
 * 
 * @author Sean
 */
public class CAShape {
	/** A reference to the protoShape that gave rise to this CAShape. */
	protected CAProtoShape protoShape;
	/** Angle that shape is rotated (in radians). */
	protected float orientation;
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

	/* Array of fundamental shapes from which more specific shapes can be found. */
	protected static final CAShape[] basicShapes = { new CARectangle(),
			new CACircle() };

	/**
	 * Singleton constructor.
	 */
	public CAShape() {
	}

	/**
	 * General constructor.
	 * 
	 * @param protoshape
	 *            The CAProtoShape that gave rise to this CAShape.
	 */
	public CAShape(CAProtoShape protoShape) {
		this.protoShape = protoShape;

		defaultColours();
	}

	/**
	 * Master constructor.
	 * 
	 * @param canvas
	 *            A picture to render identified shapes on.
	 */
	public CAShape(Picture picture) {
		graphics = picture.getImage().createGraphics();

		defaultColours();
	}

	protected void defaultColours() {
		fillColour = new Color(230, 245, 230);
		outlineColour = Color.green;
		centroidColour = Color.magenta;
		labelColour = Color.blue;
		font = DEFAULT_FONT;
	}

	/**
	 * Gets the dimensions of this CAShape, for example its width and height.
	 * 
	 * @return The dimensions of this CAShape.
	 */
	public int[] getDimensions() {
		int[][] boundaries = protoShape.getBoundaries();
		int[] dimensions = new int[boundaries[0].length];
		for (int i = 0; i < dimensions.length; i++) {
			dimensions[i] = boundaries[1][i] - boundaries[0][i];
		}
		return dimensions;
	}

	/**
	 * Gets this CAShape's centroid.
	 * 
	 * @return The centroid's coordinates.
	 */
	public int[] getCentroid() {
		int[][] boundaries = protoShape.getBoundaries();
		int[] centroid = new int[boundaries[0].length];
		for (int i = 0; i < centroid.length; i++) {
			centroid[i] = (boundaries[1][i] + boundaries[0][i]) / 2;
		}
		return centroid;
	}

	public String toString() {
		return "(" + this.getClass().getSimpleName() + ") [" + getStats()
				+ ", centroid: " + arrayToString(getCentroid())
				+ ", dimensions: " + arrayToString(getDimensions()) + "]";
	}

	protected String getStats() {
		int[] dimensions = protoShape.getDimensions();
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

	/**
	 * Draws this shape and additional information to the canvas.
	 */
	public void draw() {
		drawShape();
		drawCentroid();
		drawLabel();
	}

	/**
	 * Draws the shape. Subclasses could extend this.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawShape() {
		graphics.setColor(fillColour); // protoShape.getColour()
		for (CACell cell : protoShape.getAreaCells()) {
			int[] coordinates = cell.getCoordinates();
			graphics.fillRect(coordinates[0], coordinates[1], 1, 1);
		}
		graphics.setColor(outlineColour);
		for (CACell cell : protoShape.getOutlineCells()) {
			int[] coordinates = cell.getCoordinates();
			graphics.fillRect(coordinates[0], coordinates[1], 1, 1);
		}
	}

	/**
	 * Draws a cross at the centroid of the shape in the specified colour.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawCentroid() {
		graphics.setColor(centroidColour);
		int[] centroid = protoShape.getCentroid();

		graphics.drawLine(centroid[0] - 2, centroid[1] - 2, centroid[0] + 2,
				centroid[1] + 2);
		graphics.drawLine(centroid[0] - 2, centroid[1] + 2, centroid[0] + 2,
				centroid[1] - 2);
	}

	/**
	 * Draws a descriptive label of the shape.
	 * 
	 * @param graphics
	 *            Canvas to draw on.
	 */
	public void drawLabel() {
		int[] centroid = protoShape.getCentroid();

		drawString(getClass().getSimpleName(), centroid[0], centroid[1] - 10);
		drawString(getStats(), centroid[0], centroid[1] + 10);
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
	 * 
	 * @param protoShape
	 *            An unidentified shape.
	 * @return An instance of the detected shape.
	 */
	public CAShape identifyShape(CAProtoShape protoShape) {
		for (CAShape shape : basicShapes) {
			CAShape detectedShape = shape.identify(protoShape);
			if (detectedShape != null) {
				detectedShape.graphics = graphics;
				return detectedShape;
			}
		}
		return new CAUnknownShape(protoShape);
	}

	/**
	 * Returns an instance of the shape described by the protoShape.
	 * <p>
	 * Subclasses should extend this.
	 * 
	 * @param protoShape
	 *            An unidentified shape.
	 * @return An instance of the detected shape if detected or null otherwise.
	 */
	protected CAShape identify(CAProtoShape protoShape) {
		return null;
	}
}