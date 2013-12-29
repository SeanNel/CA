package ca.shapedetector.path;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import math.DiscreteFunction;
import math.CartesianVector;

import std.Picture;

import ca.CACell;
import ca.shapedetector.CAProtoShape;
import graphics.mygraph.LineGraph;
import graphics.mygraph.Series;

/**
 * An abstraction layer for working with paths that describe shapes.
 * 
 * @author Sean
 */
public class SDPath implements Iterable<double[]> {
	/** The internal representation of this path. */
	protected Path2D.Double path;
	/**
	 * The angle of orientation of this polygon relative to the x-axis (in
	 * radians).
	 */
	protected double orientation;
	/**
	 * The area of this shape (in pixels squared).
	 */
	protected double area;
	/**
	 * The center of gravity of this shape.
	 */
	protected double[] centroid;

	public SDPath(CAProtoShape protoShape) {
		// System.out.println("*** SDPath: " + this.hashCode());

		path = makePath(protoShape.getOutlineCells());
		/*
		 * Should fill the gaps in the path until the outline finder works in
		 * all cases.
		 */

		/*
		 * Cannot derive centroid from the areaCells when shapes contain nested
		 * shapes...
		 */
		// centroid = protoShape.calculateCentroid();
		centroid = new double[2];
		centroid[0] = path.getBounds2D().getCenterX();
		centroid[1] = path.getBounds2D().getCenterY();

		/*
		 * Alternatively, build the path from only the areaCells. But this seems
		 * to take much longer for larger shapes.
		 */
		// Stopwatch stopwatch = new Stopwatch();
		// Area area = makeArea(protoShape.getAreaCells());
		// stopwatch.print("makearea time: ");
		// stopwatch.start();
		// area = fillGaps(area);
		// stopwatch.print("fillgaps time: ");
		// path = new Path2D.Double();
		// path.append(area, true);
		// stopwatch.print("append time: ");
		// stopwatch.start();

		this.area = calculateArea();
		// CAProtoShape.clearDebugPicture();
		// displayHighlight(CAProtoShape.debugPicture);
		orientation = calculateOrientation();
	}

	/**
	 * Copy constructor.
	 * 
	 * @param original
	 */
	public SDPath(SDPath original) {
		orientation = original.orientation;
		area = original.area;
		path = (Path2D.Double) original.path.clone();
		centroid = original.centroid;

		/* The following may be slightly more efficient by skipping the cast */
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
		area = calculateArea();

		centroid = new double[2];
		centroid[0] = path.getBounds2D().getCenterX();
		centroid[1] = path.getBounds2D().getCenterY();
	}

	/**
	 * Creates a polygonal estimation of the given ellipse.
	 * 
	 * @param shape
	 */
	public SDPath(Ellipse2D shape) {
		FlatteningPathIterator pathIterator = new FlatteningPathIterator(
				shape.getPathIterator(null), 0.2); // 3.0
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

		centroid = new double[2];
		centroid[0] = path.getBounds2D().getCenterX();
		centroid[1] = path.getBounds2D().getCenterY();
	}

	/**
	 * Gets a rectangle describing the boundaries of this path.
	 * 
	 * @return Boundary rectangle.
	 */
	public Rectangle2D getBounds() {
		return path.getBounds2D();
	}

	public double getArea() {
		return area;
	}

	public double getOrientation() {
		return orientation;
	}

	public double[] getCentroid() {
		return centroid;
	}

	/**
	 * Construct a path from a list of cells describing the outline.
	 * 
	 * @param cells
	 * @return
	 */
	protected static Path2D.Double makePath(List<CACell> cells) {
		Iterator<CACell> cellIterator = cells.iterator();
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
	public static Area makeArea(List<CACell> cells) {
		Area area = new Area();
		Iterator<CACell> cellIterator = cells.iterator();
		while (cellIterator.hasNext()) {
			CACell cell = cellIterator.next();
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
	 * Gets the orientation of this path's axis of symmetry relative to the
	 * x-axis, in radians.
	 * 
	 * @return Orientation angle in radians.
	 */
	public double calculateOrientation() {
		double[] f = SDDistributionHistogram.getGradientDistribution(this);

		/*
		 * This number is arbitrary, but it affects the angular resolution and
		 * 360 is convenient because it corresponds to 2Pi.
		 */
		f = DiscreteFunction.fit(f, 360);

		SDPathIterator iterator = iterator();
		double[] first = iterator.next();

		double[] v1 = { first[0] - centroid[0], first[1] - centroid[1] };
		/* 360 - angle = anticlockwise angle from x-axis. */
		double orientation = 2.0 * Math.PI - CartesianVector.getAngle(v1);

		/*
		 * According to theory, the window should be at least twice as long as
		 * the fundamental wavelength (which is 360 deg). This does not seem to
		 * make any difference here though, except to slow to a crawl...
		 */
		// f = DiscreteFunction.loop(f, 2.0);
		// f = DiscreteFunction.crop(f, 360);

		/*
		 * The signal's autoCorrelation gives a wave that describes the
		 * symmetrical properties of the gradient wave.
		 */
		f = DiscreteFunction.autoCorrelation(f);
		// DiscreteFunction.absoluteValue(f);

		// displayHighlight(graphPicture);
		// displayGraph(f);

		/*
		 * TODO: The techniques up til this point seem to make sense, although
		 * the angles returned here do not. What went wrong?
		 */
		double x = Math.toRadians(DiscreteFunction.findValley(f));

		// System.out.println("Orientation(a): "
		// + Math.round(Math.toDegrees(orientation)));
		// System.out.println("Orientation(x): " +
		// Math.round(Math.toDegrees(x)));

		orientation += x;
		if (orientation > 2.0 * Math.PI) {
			orientation -= 2.0 * Math.PI;
		}

		System.out.println("TOTAL Orientation: "
				+ Math.round(Math.toDegrees(orientation)));
		return orientation;
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
	public void move(int x, int y) {
		/* Better to use the centroid for this? */
		// double x0 = path.getBounds2D().getCenterX();
		// double y0 = path.getBounds2D().getCenterY();

		double x0 = centroid[0];
		double y0 = centroid[1];

		path.transform(AffineTransform.getTranslateInstance(x - x0, y - y0));
	}

	/**
	 * Rotates the path anti-clockwise around it's center by the specified angle (in radians).
	 * 
	 * @param theta
	 */
	public void rotate(double theta) {
		/* Better to use the centroid for this? */
		// double x = path.getBounds2D().getCenterX();
		// double y = path.getBounds2D().getCenterY();

		double x = centroid[0];
		double y = centroid[1];

		path.transform(AffineTransform.getTranslateInstance(-x, -y));
		path.transform(AffineTransform.getRotateInstance(theta));
		path.transform(AffineTransform.getTranslateInstance(x, y));
	}

	/**
	 * Calculates the difference ratio between this path and the specified path.
	 * 
	 * @param path
	 *            The path to compare this path to.
	 * @return The difference ratio.
	 */
	public double getDifference(SDPath path) {
		// double match = getAreaDifference(path);
		double match = getGradientDifference(path);

		System.out.println("Match: " + match);
		return match;
	}

	/**
	 * 1: paths are oriented the same. 2: get gradient data. 3: scale graphs to
	 * the same domain. 4: take the integral of the absolute value of the
	 * difference between the two graphs... factor = Integrate[|f2 - f1|]
	 */
	protected double getGradientDifference(SDPath path) {
		/* Should cache these... */
		double[] f1 = SDDistributionHistogram.getGradientDistribution(this);
		double[] f2 = SDDistributionHistogram.getGradientDistribution(path);

		/*
		 * An arbitrary choice of which function to fit, but if the identity is
		 * always the same then the resultant difference is more predictable.
		 */
		f2 = DiscreteFunction.fit(f2, f1.length);

		/* Aligns the graphs according to their axes of symmetry. */
		int f1Theta = (int) Math.round(Math.toDegrees(orientation));
		int f2Theta = (int) Math.round(Math.toDegrees(orientation));
		DiscreteFunction.rotate(f1, f1Theta);
		DiscreteFunction.rotate(f2, f2Theta);

		// displayGraph(f1);
		// displayGraph(f2);

		/* Compares the resultant functions. */
		double[] f = DiscreteFunction.difference(f1, f2);
		DiscreteFunction.absoluteValue(f);
		double factor = DiscreteFunction.integrate(f);

		double n = (Math.PI / 2.0 * f.length);
		return (n - factor) / n;
	}

	protected double getAreaDifference(SDPath path) {
		path = new SDPath(path);
		scaleToIdentity(this, path);
		path.area = path.calculateArea();

		double areaDifference = Math.abs(this.getArea() - path.getArea());
		/*
		 * The returned values would be negative when the path given has a
		 * greater area than the identity shape.
		 */
		return Math.abs(1.0 - (areaDifference / this.getArea()));
	}

	public void draw(Picture picture, Color outlineColour, Color fillColour) {
		Graphics2D graphics = picture.getImage().createGraphics();
		graphics.setColor(fillColour);
		graphics.fill(path);

		graphics.setColor(outlineColour);
		graphics.draw(path);
	}

	public static void scaleToIdentity(SDPath identity, SDPath path) {
		/* Assumes that the path has been rotated appropriately. */
		Rectangle2D identityBounds = identity.getBounds();
		path.resize(identityBounds);
	}

	/*
	 * The drawing methods that follow are pretty rough, but they were just used
	 * while working on the other methods, never mind them...
	 */

	public static int debugI = 0;

	public static void displayIdentification(SDPath identity,
			SDPath unidentified) {
		Picture debugPicture = new Picture(400, 400);
		debugPicture.setOriginUpperLeft();
		Graphics2D g = debugPicture.getImage().createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, 400, 400);
		Rectangle bounds = new Rectangle(0, 0, 200, 200);

		identity = new SDPath(identity);
		unidentified = new SDPath(unidentified);
		// unidentified.rotate(unidentified.orientation);
		unidentified.resize(bounds);
		identity.resize(bounds);
		unidentified.move(200, 200);
		identity.move(200, 200);
		unidentified.draw(debugPicture, new Color(255, 200, 0, 230), new Color(
				230, 200, 0, 50));
		identity.draw(debugPicture, new Color(0, 255, 255, 230), new Color(0,
				230, 230, 50));

		String str1 = "(Shape) area=" + unidentified.getArea()
				+ ", orientation="
				+ Math.round(Math.toDegrees(unidentified.getOrientation()));
		String str2 = "(Identity) area=" + identity.getArea()
				+ ", orientation="
				+ Math.round(Math.toDegrees(identity.getOrientation()));
		String str3 = "(" + debugI++ + ") Press space to continue...";
		g.setColor(Color.black);
		g.drawString(str1, 10, 20);
		g.drawString(str2, 10, 35);
		g.drawString(str3, 10, 385);

		debugPicture.show();
	}

	public static Picture debugPicture;

	public void displayHighlight(Picture picture) {
		if (debugPicture == null) {
			debugPicture = new Picture(picture);
		}
		debugPicture.getImage().createGraphics()
				.drawImage(picture.getImage(), 0, 0, null);
		Graphics2D graphics = debugPicture.getImage().createGraphics();
		draw(debugPicture, new Color(255, 200, 0, 230), new Color(230, 200, 0,
				50));

		String str1 = "(Shape) area=" + getArea();
		String str2 = "orientation=" + getOrientation() + " rad ("
				+ Math.toDegrees(getOrientation()) + " deg)";
		String str3 = "Press space to continue...";
		graphics.setColor(Color.black);
		graphics.drawString(str1, 10, 20);
		graphics.drawString(str2, 10, 35);
		graphics.drawString(str3, 10, 385);

		debugPicture.show();
	}

	static Picture graphPicture = new Picture(500, 500);

	protected void displayGraph(double[] data) {
		graphPicture.setOriginUpperLeft();
		Graphics2D graphics = graphPicture.getImage().createGraphics();
		graphics.setColor(Color.white);
		graphics.fillRect(0, 0, 500, 500);

		LineGraph linegraph = new LineGraph(graphPicture);
		linegraph.move(100, 100);
		linegraph.resize(350, 350);

		Series<Double> theta_series = new Series<Double>("theta", new Color(0,
				0, 0, 200));
		linegraph.addSeries(theta_series);

		// System.out.println("***");
		for (double element : data) {
			// System.out.println(">" + element);
			theta_series.add(element);
		}

		linegraph.render();
		graphPicture.show();
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

		double[] vector = CartesianVector.getRelative(v1, v2);
		double angle = CartesianVector.getAngle(vector);
		if (angle > Math.PI) {
			angle -= Math.PI;
		}
		return angle;
	}

	@Override
	public SDPathIterator iterator() {
		return new SDPathIterator(path);
	}

}