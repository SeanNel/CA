package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.AbstractShape;
import ca.shapedetector.shapes.UnknownShape;

public class SDPanel extends PicturePanel {
	private static final long serialVersionUID = 1L;

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
	protected int padding = 10;
	protected double[] drawCursor;

	public SDPanel() {
		setTheme(SDPanelTheme.DEFAULT);
		drawCursor = new double[2];
	}

	/**
	 * Clears the image and draws the shape in the centre.
	 * 
	 * @param shape
	 * @return the position where the shape was drawn.
	 */
	public synchronized void display(AbstractShape shape) {
		shape = new UnknownShape(shape);
		shape.getPath().move(drawCursor[0], drawCursor[1]);

		draw(shape);
	}

	public synchronized void reset(int w, int h) {
		w += padding;
		h += padding;
		setImage(new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB));
		clear();
		drawCursor[0] = w / 2;
		drawCursor[1] = h / 2;
	}

	public synchronized void moveDrawCursor(double[] drawCursor) {
		this.drawCursor = drawCursor;
	}

	public synchronized void moveDrawCursor(double x, double y) {
		drawCursor[0] = x;
		drawCursor[1] = y;
	}

	public double[] getDrawCursor() {
		return drawCursor;
	}

	/**
	 * Draws the shape in place without clearing the image.
	 * 
	 * @param shape
	 */
	public synchronized void draw(AbstractShape shape) {
		shape.getPath().draw(graphics, outlineColour, fillColour);

		drawCentroid(shape);
		drawLabel(shape);

		repaint();
	}

	public synchronized void draw(SDPath path) {
		path.draw(graphics, outlineColour, fillColour);
		repaint();
	}

	/**
	 * Draws a cross at the centroid of the shape in the specified colour.
	 */
	public synchronized void drawCentroid(AbstractShape shape) {
		Point2D centroid = shape.getPath().getCentroid();
		graphics.setColor(centroidColour);
		int centroidX = (int) centroid.getX();
		int centroidY = (int) centroid.getY();

		graphics.drawLine(centroidX - 2, centroidY - 2, centroidX + 2,
				centroidY + 2);
		graphics.drawLine(centroidX - 2, centroidY + 2, centroidX + 2,
				centroidY - 2);
	}

	/**
	 * Draws a descriptive label of the shape.
	 */
	public synchronized void drawLabel(AbstractShape shape) {
		Point2D centroid = shape.getPath().getCentroid();
		int centroidX = (int) centroid.getX();
		int centroidY = (int) centroid.getY();

		drawString(shape.getClass().getSimpleName(), centroidX, centroidY - 10);
		// drawString(getDescription(), centroidX, centroidY + 10);
		drawString("x=" + centroidX + ", y=" + centroidY, centroidX,
				centroidY + 10);
	}

	/**
	 * Draws a string.
	 */
	public synchronized void drawString(String string, int x, int y) {
		graphics.setColor(labelColour);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();

		int ws = metrics.stringWidth(string);
		int hs = metrics.getDescent();

		graphics.drawString(string, (int) (x - ws / 2.0), (float) (y + hs));
	}

	public synchronized void setTheme(SDPanelTheme theme) {
		switch (theme) {
		case UNRECOGNIZED:
			labelColour = new Color(0, 0, 0, 40);
			outlineColour = new Color(255, 255, 0, 40);
			centroidColour = new Color(200, 50, 0, 40);
			break;
		case MASK:
			outlineColour = Color.green;
			labelColour = new Color(0, 0, 0, 0);
			fillColour = new Color(230, 255, 250, 120);
			break;
		case SIMPLE:
			outlineColour = Color.blue;
			labelColour = new Color(0, 0, 0, 0);
			break;
		case HIGHLIGHT:
			outlineColour = Color.red;
			fillColour = new Color(255, 245, 230, 100);
		default:
			fillColour = new Color(230, 245, 230, 100);
			outlineColour = Color.red;
			centroidColour = Color.magenta;
			labelColour = Color.blue;
			font = DEFAULT_FONT;
		}
	}

}
