package ca.rules.shape;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.SDShape;
import ca.shapedetector.shapes.SDUnknownShape;

/**
 * Displays all the found shapes on the output image.
 */
public class SDShapeDrawRule extends SDShapeRule {
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

	protected static Graphics2D graphics;

	public SDShapeDrawRule(CAShapeDetector ca) {
		super(ca);

		graphics = ca.getPicture().getImage().createGraphics();
		defaultColours();
	}

	protected void defaultColours() {
		fillColour = new Color(230, 245, 230, 100);
		outlineColour = Color.red;
		centroidColour = Color.magenta;
		labelColour = Color.blue;
		font = DEFAULT_FONT;
	}

	protected void unrecognizedColours() {
		fillColour = new Color(200, 200, 200, 100);
		outlineColour = Color.yellow;
	}

	protected void recognizedColours() {
		fillColour = new Color(230, 245, 230, 100);
		outlineColour = Color.green;
	}

	public void update(SDShape shape) {
		/* Ignores the rectangle detected at the image borders. */
		// int delta = 2;
		// if (shape instanceof SDRectangle
		// && shape.getDimensions()[0] + delta > pictureBefore.width()
		// && shape.getDimensions()[1] + delta > pictureBefore
		// .height()) {
		// continue;
		// }

		/* using instanceof does not seem to work here. */

		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		SDPath path = shape.getPath();

		if (shape.getClass() != SDUnknownShape.class) {
			recognizedColours();
			path.draw(graphics, outlineColour, fillColour);
			drawCentroid(shape);
			drawLabel(shape);
		} else {
			unrecognizedColours();
			path.draw(graphics, outlineColour, fillColour);
		}
		// Input.waitForSpace();
	}

	/**
	 * Draws a cross at the centroid of the shape in the specified colour.
	 */
	public void drawCentroid(SDShape shape) {
		double[] centroid = shape.getCentroid();
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
	public void drawLabel(SDShape shape) {
		double[] centroid = shape.getCentroid();
		int centroidX = (int) centroid[0];
		int centroidY = (int) centroid[1];

		drawString(shape.getClass().getSimpleName(), centroidX, centroidY - 10);
		// drawString(getDescription(), centroidX, centroidY + 10);
		drawString("x=" + centroidX + ", y=" + centroidY, centroidX,
				centroidY + 10);
	}

	/**
	 * Draws a string.
	 */
	public void drawString(String string, int x, int y) {
		graphics.setColor(labelColour);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();

		int ws = metrics.stringWidth(string);
		int hs = metrics.getDescent();

		graphics.drawString(string, (int) (x - ws / 2.0), (float) (y + hs));
	}
}
