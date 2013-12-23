package ca.shapedetector.shapes;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import std.Picture;

import ca.CACell;
import ca.shapedetector.CAProtoShape;

/**
 * An abstraction layer for working with paths that describe shapes.
 * 
 * @author Sean
 */
public class SDPath {
	/** The internal representation of this path. */
	protected Path2D.Double path;
	/**
	 * The angle of orientation of this polygon relative to the x-axis (in
	 * radians).
	 */
	protected double orientation;

	public SDPath(CAProtoShape protoShape) {
		Iterator<CACell> cellIterator = protoShape.getOutlineCells().iterator();
		int[] coordinates = cellIterator.next().getCoordinates();
		path = new Path2D.Double();
		path.moveTo(coordinates[0], coordinates[1]);
		while (cellIterator.hasNext()) {
			coordinates = cellIterator.next().getCoordinates();
			path.lineTo(coordinates[0], coordinates[1]);
		}
		path.closePath();

		// reduceVertices();
		orientation = calculateOrientation();
		/* For debugging */
		// rotate(orientation);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param original
	 */
	public SDPath(SDPath original) {
		orientation = original.orientation;
		path = (Path2D.Double) original.path.clone();

		// path = new Path2D.Double();
		// path.append(original.path, true);
	}

	/**
	 * Creates an SDPath representation of a general shape, rotated such that
	 * its major axis lies parallel to the x-axis.
	 * 
	 * @see java.awt.geom.Shape
	 * @param shape
	 */
	public SDPath(Shape shape) {
		path = new Path2D.Double();
		path.append(shape, true);

		// orientation = calculateOrientation();
		// rotate(-orientation);
	}

	/**
	 * Creates a polygonal estimation of the given ellipse, rotated such that
	 * its major axis lies parallel to the x-axis.
	 * 
	 * @param shape
	 */
	public SDPath(Ellipse2D shape) {
		FlatteningPathIterator pathIterator = new FlatteningPathIterator(
				shape.getPathIterator(null), 3.0);
		double[] coordinates = new double[6];
		pathIterator.currentSegment(coordinates);

		path = new Path2D.Double();
		path.moveTo(coordinates[0], coordinates[1]);
		pathIterator.next();

		while (!pathIterator.isDone()) {
			pathIterator.currentSegment(coordinates);
			path.lineTo(coordinates[0], coordinates[1]);
			pathIterator.next();
		}
		path.closePath();

		// orientation = calculateOrientation();
		// rotate(-orientation);
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
	 * Gets the area enclosed by the path. Formula source:
	 * http://www.mathopenref.com/coordpolygonarea.html
	 * 
	 * @return Area in pixels squared.
	 */
	public double getArea() {
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
	 * Gets the orientation of this path relative to the x-axis, in radians.
	 * 
	 * @return Orientation angle in radians.
	 */
	protected double calculateOrientation() {
		List<Double> azimuthDistribution = getAzimuthHistogram(16);
		/*
		 * TODO: find the (principal) vertical line of symmetry from the data,
		 * which is the angle of the axis of symmetry, aka the orientation we
		 * seek.
		 */

		/* Until this method is finished, use results from the diameter method: */
		return calculateDiameterOrientation();
	}

	/**
	 * Gets the area distribution of the path. Greater precision is achieved
	 * with more sectors.
	 * <p>
	 * Casts triangles subtended by the centroid of the path, with base vertices
	 * along a circle that encloses the path. Returns a list of the areas
	 * enclosed by the path in each sector.
	 * <p>
	 * Triangles were chosen because they are simple to implement, fast to
	 * calculate, and do not overlap. It may or may not be necessary to extend
	 * them out such that the base of each triangle is tangent to the circle.
	 * <p>
	 * It may also be better to use the center of gravity instead of the center
	 * of the path boundaries. This could be gathered from adding all the
	 * outlineCells x and y positions together to get a weighted center value.
	 * 
	 * @return The number of sectors to divide the azimuth into.
	 */
	protected List<Double> getAzimuthHistogram(int numSectors) {
		double sweep = 2.0 * Math.PI / (double) numSectors;
		double radius;
		if (path.getBounds2D().getWidth() > path.getBounds2D().getHeight()) {
			radius = path.getBounds2D().getWidth() / 2.0;
		} else {
			radius = path.getBounds2D().getHeight() / 2.0;
		}
		double sectorArea = radius * radius * Math.sin(sweep / 2.0)
				* Math.cos(sweep / 2.0);

		double centroidX = path.getBounds2D().getCenterX();
		double centroidY = path.getBounds2D().getCenterY();
		Area pathShape = new Area(path);

		List<Double> azimuthHistogram = new ArrayList<Double>(numSectors);

		//
		// StdDraw.clear();
		// StdDraw.setXscale();
		// StdDraw.setYscale();
		//
		// LineGraph linegraph = new LineGraph();
		// linegraph.resize(0.8, 1.0);
		// Series azimuth_series = new Series("azimuth", new Color(0, 255, 0,
		// 150));
		//
		// linegraph.addSeries(azimuth_series);
		//
		for (int i = 0; i < numSectors; i++) {
			double theta = sweep * i;
			double x1 = centroidX + radius * Math.cos(theta);
			double y1 = centroidY + radius * Math.sin(theta);
			double x2 = centroidX + radius * Math.cos(theta + sweep);
			double y2 = centroidY + radius * Math.sin(theta + sweep);

			Path2D.Double sector = new Path2D.Double();
			sector.moveTo(centroidX, centroidY);
			sector.lineTo(x1, y1);
			sector.lineTo(x2, y2);
			sector.closePath();

			Area sectorShape = new Area(sector);
			sectorShape.subtract(pathShape);
			SDPath projectionPath = new SDPath(sectorShape);
			double projectionArea = sectorArea - projectionPath.getArea();
			azimuthHistogram.add(projectionArea);

			// azimuth_series.add(projectionArea);

		}
		// linegraph.render();
		// waitForSpace();
		return azimuthHistogram;
	}

	/**
	 * Gets the orientation of the polygon's diameter, relative to the x-axis,
	 * in radians.
	 * <p>
	 * Using the orientation of the polygon's axis of symmetry instead would be
	 * MUCH better.
	 * <p>
	 * Uses brute force method of comparing every point along the path to every
	 * other point to find the maximum distance between any two points, then
	 * finds the angle between those points. The returned angle is < 180 because
	 * angles greater than this are ambiguous, depending on whether the angle is
	 * measured from the 1st vector to the 2nd, or the other way around.
	 * 
	 * @return Orientation angle in radians.
	 */
	protected double calculateDiameterOrientation() {
		PathIterator pathIterator = path.getPathIterator(null);
		double[] v1 = new double[2];
		double[] v2 = new double[2];
		double[] coordinates1 = new double[6];
		double[] coordinates2 = new double[6];
		double maxDistance = 0;

		while (!pathIterator.isDone()) {
			pathIterator.currentSegment(coordinates1);
			pathIterator.next();

			PathIterator pathIterator2 = path.getPathIterator(null);
			while (!pathIterator2.isDone()) {
				pathIterator2.currentSegment(coordinates2);
				pathIterator2.next();

				double x = coordinates1[0] - coordinates2[0];
				double y = coordinates1[1] - coordinates2[1];
				/*
				 * distance = Math.sqrt(x * x + y * y) but it is more efficient
				 * to use the distance squared to compare distances.
				 */
				double distanceSquared = x * x + y * y;

				if (distanceSquared > maxDistance) {
					v1[0] = coordinates1[0];
					v1[1] = coordinates1[1];
					v2[0] = coordinates2[0];
					v2[1] = coordinates2[1];
					maxDistance = distanceSquared;
				}
			}
		}

		double angle = getAngle(v1, v2);
		if (angle > Math.PI) {
			angle -= Math.PI;
		}
		return angle;
	}

	public double getOrientation() {
		return orientation;
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
	 * Moves this path to the specified position.
	 * 
	 * @param x
	 * @param y
	 */
	public void move(int x, int y) {
		double x0 = path.getBounds2D().getCenterX();
		double y0 = path.getBounds2D().getCenterY();

		path.transform(AffineTransform.getTranslateInstance(x - x0, y - y0));
	}

	/**
	 * Rotates the path anti-clockwise by the specified angle (in radians).
	 * 
	 * @param theta
	 */
	public void rotate(double theta) {
		double x = path.getBounds2D().getCenterX();
		double y = path.getBounds2D().getCenterY();

		path.transform(AffineTransform.getTranslateInstance(-x, -y));
		path.transform(AffineTransform.getRotateInstance(-theta));
		path.transform(AffineTransform.getTranslateInstance(x, y));
	}

	/**
	 * Gets the counter-clockwise angle from the 1st vertex to the 2nd, relative
	 * to the x-axis, in radians. This angle is always positive. (Assumes 2D)
	 * 
	 * @param v1
	 *            2-dimensional array of coordinates to 1st vector.
	 * @param v2
	 *            2-dimensional array of coordinates to 2nd vector.
	 * @return
	 */
	public static double getAngle(double[] v1, double[] v2) {
		double x = v2[0] - v1[0];
		double y = v2[1] - v1[1];

		if (x == 0) {
			return Math.PI / 2.0;
		} else {
			double angle = Math.atan(y / x);
			if (angle < 0) {
				angle += Math.PI * 2.0;
			}
			if (x < 0 && y < 0) {
				angle += Math.PI;
			}
			return angle;
		}
	}

	/**
	 * Calculates the difference ratio between this path and the specified path
	 * by comparing their areas.
	 * 
	 * @param path
	 *            The path to compare this path to.
	 * @return The difference ratio.
	 */
	public double getDifference(SDPath path) {
		path = new SDPath(path);
		/* Assumes that the path has been rotated correctly. */
		scaleToIdentity(this, path);

		/* Take a look at how the comparison is done... */
		// debugIdentify(this, path);

		/*
		 * TODO: May want to cache the areas of shapes to speed up future
		 * comparisons.
		 */

		double areaDifference = Math.abs(this.getArea() - path.getArea());
		/*
		 * The returned values would be negative when the path given has a
		 * greater area than the identity shape.
		 */
		double match = Math.abs(1.0 - (areaDifference / this.getArea()));
		// System.out.println(match);
		return match;
	}

	public void draw(Graphics2D graphics, Color outlineColour, Color fillColour) {
		graphics.setColor(fillColour);
		graphics.fill(path);

		graphics.setColor(outlineColour);
		graphics.draw(path);
	}

	public static void scaleToIdentity(SDPath identity, SDPath path) {
		/* Assumes that the path has been rotated appropriately. */
		Rectangle2D identityBounds = identity.getBounds();
		// Rectangle2D pathBounds = path.getBounds2D();
		path.resize(identityBounds);
	}

	public static final Picture debugPicture = new Picture(400, 400);
	public static int debugI = 0;

	public void debugIdentify(SDPath identity, SDPath unidentified) {
		Rectangle bounds = new Rectangle(0, 0, 200, 200);
		Graphics2D g = debugPicture.getImage().createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 400, 400);

		identity = new SDPath(identity);
		unidentified = new SDPath(unidentified);
		// unidentified.rotate(unidentified.orientation);
		unidentified.resize(bounds);
		identity.resize(bounds);
		unidentified.move(200, 200);
		identity.move(200, 200);
		unidentified.draw(g, new Color(255, 200, 0, 230), new Color(230, 200,
				0, 50));
		identity.draw(g, new Color(0, 255, 255, 230),
				new Color(0, 230, 230, 50));

		String str1 = "(Shape) area=" + unidentified.getArea()
				+ ", orientation=" + identity.getOrientation();
		String str2 = "(Identity) area=" + identity.getArea()
				+ ", orientation=" + identity.getOrientation();
		String str3 = "(" + debugI++ + ") Press space to continue...";
		g.setColor(Color.black);
		g.drawString(str1, 10, 20);
		g.drawString(str2, 10, 35);
		g.drawString(str3, 10, 385);

		debugPicture.show();
		waitForSpace();
	}

	public void debugPaths(SDPath identity, SDPath unidentified) {
		Rectangle bounds = new Rectangle(0, 0, 400, 400);
		Graphics2D g = debugPicture.getImage().createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 400, 400);

		identity = new SDPath(identity);
		unidentified = new SDPath(unidentified);
		// identity.path.transform(AffineTransform.getTranslateInstance(300,
		// 300));
		// unidentified.path.transform(AffineTransform.getTranslateInstance(300,
		// 300));
		// unidentified.rotate(unidentified.orientation);
		unidentified.resize(bounds);
		identity.resize(bounds);
		unidentified.move(200, 200);
		identity.move(200, 200);
		unidentified.draw(g, new Color(255, 200, 0, 230), new Color(230, 200,
				0, 50));
		identity.draw(g, new Color(0, 255, 255, 230),
				new Color(0, 230, 230, 50));

		String str1 = "(Shape) area=" + unidentified.getArea()
				+ ", orientation=" + identity.getOrientation();
		String str2 = "(Identity) area=" + identity.getArea()
				+ ", orientation=" + identity.getOrientation();
		String str3 = "(" + debugI++ + ") Press space to continue...";
		g.setColor(Color.black);
		g.drawString(str1, 10, 20);
		g.drawString(str2, 10, 35);
		g.drawString(str3, 10, 385);

		debugPicture.show();
		waitForSpace();
	}

	public void waitForSpace() {
		final CountDownLatch latch = new CountDownLatch(2);
		KeyEventDispatcher dispatcher = new KeyEventDispatcher() {
			// Anonymous class invoked from EDT
			public boolean dispatchKeyEvent(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_SPACE)
					latch.countDown();
				return false;
			}
		};
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.addKeyEventDispatcher(dispatcher);
		try {
			latch.await();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} // current thread waits here until countDown() is called
		KeyboardFocusManager.getCurrentKeyboardFocusManager()
				.removeKeyEventDispatcher(dispatcher);
	}

}