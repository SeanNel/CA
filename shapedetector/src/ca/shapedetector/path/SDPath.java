package ca.shapedetector.path;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import ca.Cell;
import ca.shapedetector.blob.Blob;

/**
 * An abstraction layer for working with paths that describe shapes.
 * 
 * @author Sean
 */
public class SDPath implements Iterable<double[]> {
	/** The internal representation of this path. */
	protected Path2D.Double path;

	public SDPath(Blob blob) {
		path = makePath(blob.getOutlineCells());

		/*
		 * Alternatively, build the path from only the areaCells. But this seems
		 * to take much longer for larger shapes.
		 */
		// Stopwatch stopwatch = new Stopwatch();
		// Area area = makeArea(blob.getAreaCells());
		// stopwatch.print("makearea time: ");
		// stopwatch.start();
		// area = fillGaps(area);
		// stopwatch.print("fillgaps time: ");
		// path = new Path2D.Double();
		// path.append(area, true);
		// stopwatch.print("append time: ");
		// stopwatch.start();

		/*
		 * TODO: May need to fill the gaps in the path until the outline finder
		 * works in all cases.
		 */
	}

	/**
	 * Copy constructor.
	 * 
	 * @param original
	 */
	public SDPath(SDPath original) {
		path = (Path2D.Double) original.path.clone();

		/*
		 * The following may or may not be slightly more efficient by avoiding
		 * the cast.
		 */
		// path = new Path2D.Double();
		// path.append(original.path, true);
	}

	/**
	 * Creates an SDPath representation of a general shape.
	 * 
	 * @see java.awt.geom.Shape
	 * @param shape
	 */
	public SDPath(Shape shape) {
		path = new Path2D.Double();
		path.append(shape, true);
	}

	/**
	 * Gets a rectangle describing the boundaries of this path.
	 * 
	 * @return Boundary rectangle.
	 */
	public Rectangle2D getBounds() {
		return path.getBounds2D();
	}

	/**
	 * Construct a path from a list of cells describing the outline.
	 * 
	 * @param cells
	 * @return
	 */
	protected static Path2D.Double makePath(List<Cell> cells) {
		Iterator<Cell> cellIterator = cells.iterator();
		int[] coordinates = cellIterator.next().getCoordinates();
		Path2D.Double path = new Path2D.Double();
		path.moveTo(coordinates[0], coordinates[1]);
		while (cellIterator.hasNext()) {
			coordinates = cellIterator.next().getCoordinates();
			path.lineTo(coordinates[0], coordinates[1]);
		}
		path.closePath();
		return path;
	}

	/**
	 * Construct a path from a list of cells describing the area.
	 * 
	 * @param cells
	 * @return
	 */
	public static Area makeArea(List<Cell> cells) {
		Area area = new Area();
		Iterator<Cell> cellIterator = cells.iterator();
		while (cellIterator.hasNext()) {
			Cell cell = cellIterator.next();
			int[] coordinates = cell.getCoordinates();
			Rectangle2D rectangle = new Rectangle2D.Double(coordinates[0],
					coordinates[1], 1, 1);
			area.add(new Area(rectangle));
		}

		return area;
	}

	/**
	 * Fills in gaps of an area.
	 * 
	 * @param cells
	 * @return
	 */
	public static Area fillGaps(Area area) {
		area = new Area(area);
		PathIterator pathIterator = area.getPathIterator(null);
		double[] coordinates = new double[6];
		Path2D path = new Path2D.Double();
		path.moveTo(coordinates[0], coordinates[1]);

		while (!pathIterator.isDone()) {
			int type = pathIterator.currentSegment(coordinates);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				path.closePath();
				Area segmentArea = new Area(path);
				area.add(segmentArea);
				path = new Path2D.Double();
				path.moveTo(coordinates[0], coordinates[1]);
				break;
			case PathIterator.SEG_LINETO:
				path.lineTo(coordinates[0], coordinates[1]);
				break;
			}

			pathIterator.next();
		}
		return area;
	}

	/**
	 * Gets the area enclosed by the path. Formula source:
	 * http://www.mathopenref.com/coordpolygonarea.html
	 * 
	 * @return Area in pixels squared.
	 */
	public double calculateArea() {
		PathIterator pathIterator1 = path.getPathIterator(null);
		PathIterator pathIterator2 = path.getPathIterator(null);
		pathIterator2.next();

		double[] coordinates1 = new double[6];
		double[] coordinates2 = new double[6];
		double area = 0;

		while (!pathIterator2.isDone()) {
			pathIterator1.currentSegment(coordinates1);
			pathIterator2.currentSegment(coordinates2);

			area += coordinates1[0] * coordinates2[1] - coordinates1[1]
					* coordinates2[0];
			pathIterator1.next();
			pathIterator2.next();
		}
		/* Includes the last point along the path as well. */
		pathIterator2 = path.getPathIterator(null);
		pathIterator1.currentSegment(coordinates1);
		pathIterator2.currentSegment(coordinates2);

		area += coordinates1[0] * coordinates2[1] - coordinates1[1]
				* coordinates2[0];

		return Math.abs(area / 2.0);
	}

	/**
	 * Scales this path to the specified size.
	 * 
	 * @param bounds
	 */
	public void resize(Rectangle2D bounds) {
		Rectangle2D currentBounds = path.getBounds2D();
		double x = currentBounds.getWidth();
		double y = currentBounds.getHeight();

		path.transform(AffineTransform.getScaleInstance(bounds.getHeight() / x,
				bounds.getWidth() / y));
	}

	/**
	 * Moves this path (with regards to its center) to the specified position.
	 * 
	 * @param x
	 * @param y
	 */
	public void move(double x, double y) {
		double x0 = path.getBounds2D().getCenterX();
		double y0 = path.getBounds2D().getCenterY();

		path.transform(AffineTransform.getTranslateInstance(x - x0, y - y0));
	}

	/**
	 * Rotates the path anti-clockwise around its center by the specified angle
	 * (in radians).
	 * 
	 * @param theta
	 */
	public void rotate(double theta) {
		/* Better to use the centre of gravity for this? */
		double x = path.getBounds2D().getCenterX();
		double y = path.getBounds2D().getCenterY();

		path.transform(AffineTransform.getTranslateInstance(-x, -y));
		path.transform(AffineTransform.getRotateInstance(theta));
		path.transform(AffineTransform.getTranslateInstance(x, y));
	}

	public double getCentreX() {
		return path.getBounds2D().getCenterX();
	}

	public double getCentreY() {
		return path.getBounds2D().getCenterY();
	}

	@Override
	public SDPathIterator iterator() {
		return new SDPathIterator(path);
	}

	// public SDPathIterator reverseIterator() {
	// return new SDPathIterator(path, true);
	// }

	public Area getAreaPolygon() {
		return new Area(path);
	}

	/**
	 * Gets an array of points on the outline of the shape.
	 * <p>
	 * Should work on a custom Path2D implementation that can give this
	 * directly, without iteration.
	 * 
	 * @return
	 */
	public double[][] getOutline() {
		SDPathIterator iterator = iterator();
		int i = 0;
		while (iterator.hasNext()) {
			iterator.next();
			i++;
		}

		double[][] outline = new double[i][2];
		i = 0;
		iterator = iterator();
		while (iterator.hasNext()) {
			outline[i++] = iterator.next();
		}

		return outline;
	}

	public void draw(Graphics2D graphics, Color outlineColour, Color fillColour) {
		graphics.setColor(fillColour);
		graphics.fill(path);

		graphics.setColor(outlineColour);
		graphics.draw(path);
	}
}