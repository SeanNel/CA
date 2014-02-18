package graphics;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import path.SDPath;

import shapes.AbstractShape;
import shapes.UnknownShape;


public class SDPanel extends PicturePanel {
	private static final long serialVersionUID = 1L;

	/** Shape's fill colour. */
	protected Color fillColour;
	/** Shape's outline colour. */
	protected Color outlineColour;
	/** Shape's centroid colour. */
	protected Color centroidColour;
	/** Shape's starting point colour. */
	protected Color startPointColour;
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
	public synchronized void display(final AbstractShape abstractShape) {
		AbstractShape shape = new UnknownShape(abstractShape);
		shape.getPath().move(drawCursor[0], drawCursor[1]);

		draw(shape);
	}

	public synchronized void reset(final int width, final int height) {
		int w = width + padding;
		int h = height + padding;
		setImage(new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB));
		clear();
		drawCursor[0] = w / 2;
		drawCursor[1] = h / 2;
	}

	public synchronized void moveDrawCursor(final double[] drawCursor) {
		this.drawCursor = drawCursor;
	}

	public synchronized void moveDrawCursor(final double x, final double y) {
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
	public synchronized void draw(final AbstractShape shape) {
		shape.getPath().draw(graphics, outlineColour, fillColour);

		drawCentroid(shape);
		drawLabel(shape);
		drawStartPoint(shape);

		repaint();
	}

	public synchronized void draw(final SDPath path) {
		path.draw(graphics, outlineColour, fillColour);
		repaint();
	}

	/**
	 * Draws a cross at the centroid of the shape in the specified colour.
	 */
	public synchronized void drawCentroid(final AbstractShape shape) {
		Point2D centroid = shape.getPath().getCentroid();
		graphics.setColor(centroidColour);
		int centroidX = (int) centroid.getX();
		int centroidY = (int) centroid.getY();

		graphics.drawLine(centroidX - 2, centroidY - 2, centroidX + 2,
				centroidY + 2);
		graphics.drawLine(centroidX - 2, centroidY + 2, centroidX + 2,
				centroidY - 2);
	}

	public synchronized void drawStartPoint(final AbstractShape shape) {
		if (shape.getPath().getVertices().size() > 0) {
			graphics.setColor(startPointColour);
			Point2D startPoint = shape.getPath().getVertices().get(0);
			int startPointX = (int) startPoint.getX();
			int startPointY = (int) startPoint.getY();

			graphics.drawOval(startPointX - 2, startPointY - 2, 4, 4);
		}
	}

	/**
	 * Draws a descriptive label of the shape.
	 */
	public synchronized void drawLabel(final AbstractShape shape) {
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
	public synchronized void drawString(final String string, final int x,
			final int y) {
		graphics.setColor(labelColour);
		graphics.setFont(font);
		FontMetrics metrics = graphics.getFontMetrics();

		int ws = metrics.stringWidth(string);
		int hs = metrics.getDescent();

		graphics.drawString(string, (int) (x - ws / 2.0), (float) (y + hs));
	}

	public synchronized void setTheme(final SDPanelTheme theme) {
		switch (theme) {
		case UNRECOGNIZED:
			labelColour = new Color(0, 0, 0, 40);
			outlineColour = new Color(255, 255, 0, 40);
			centroidColour = new Color(200, 50, 0, 40);
			startPointColour = new Color(200, 50, 0, 0);
			break;
		case MASK:
			outlineColour = Color.green;
			labelColour = new Color(0, 0, 0, 0);
			fillColour = new Color(230, 255, 250, 100);
			centroidColour = new Color(210, 235, 200, 230);
			startPointColour = new Color(210, 235, 200, 220);
			break;
		case SIMPLE:
			outlineColour = Color.blue;
			labelColour = new Color(0, 0, 0, 0);
			break;
		case HIGHLIGHT:
			outlineColour = Color.pink;
			fillColour = new Color(255, 45, 30, 150);
			break;
		case BG:
			Color g = new Color(0, 255, 255, 100);
			outlineColour = g;
			fillColour = g;
			break;
		default:
			fillColour = new Color(230, 245, 230, 100);
			outlineColour = Color.red;
			centroidColour = Color.magenta;
			labelColour = Color.blue;
			startPointColour = Color.pink;
			font = DEFAULT_FONT;
		}
	}

	/**
	 * For debugging. Clears the frame and displays the shape.
	 * 
	 * @param shape
	 */
	public static void displayShape(final AbstractShape shape,
			final SDPanelTheme theme) {
		ShapeFrame.reset(shape);
		ShapeFrame.setTheme(theme);
		ShapeFrame.display(shape);
	}

	/**
	 * For debugging. Displays the shape superimposed on an existing one.
	 * 
	 * @param shape
	 */
	public static void displayShape(final AbstractShape shape,
			final AbstractShape mask, final SDPanelTheme theme) {
		double[] cursor = ShapeFrame.getDrawCursor();

		Rectangle2D a = shape.getPath().getBounds();
		Rectangle2D b = mask.getPath().getBounds();

		double x = b.getCenterX() - a.getCenterX() + cursor[0];
		double y = b.getCenterY() - a.getCenterY() + cursor[1];

		ShapeFrame.moveDrawCursor(x, y);
		ShapeFrame.setTheme(theme);
		ShapeFrame.display(mask);
	}

	public Graphics2D getGraphics2D() {
		return graphics;
	}

}
