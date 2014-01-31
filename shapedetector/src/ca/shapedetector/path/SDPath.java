package ca.shapedetector.path;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An abstraction layer for working with closed, polygonal paths that do not
 * intersect themselves.
 * 
 * @param V
 * 
 * @author Sean
 */
public class SDPath implements Iterable<Point2D> {
	/** The internal representation of this path. */
	protected List<Point2D> vertices;
	/** The bounding rectangle. */
	protected Rectangle2D bounds;
	/** The centre of gravity. */
	protected Point2D centroid;
	/** The surface area. Includes any enveloped shapes. */
	protected double area;
	/**
	 * Maps vertices to their distances from the starting point along the
	 * oueline.
	 */
	protected OutlineMap outlineMap;
	/** A Path2D instance for drawing graphics. */
	protected Path2D.Double path2D;

	/**
	 * Constructor. Creates an empty path with no vertices.
	 */
	public SDPath() {
		vertices = new ArrayList<Point2D>();
	}

	/**
	 * Constructor.
	 */
	public SDPath(final List<Point2D> vertices) {
		addVertices(vertices);
	}

	/**
	 * Removes any existing vertices and replaces them with those from the
	 * parameter.
	 * 
	 * @param vertices
	 */
	public void addVertices(final List<Point2D> vertices) {
		this.vertices = new ArrayList<Point2D>(vertices);
		outlineMap = null;
		area = 0.0;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param original
	 */
	public SDPath(final SDPath original) {
		List<Point2D> originalVertices = original.getVertices();
		vertices = new ArrayList<Point2D>(originalVertices.size());
		for (Point2D point : originalVertices) {
			vertices.add((Point2D) point.clone());
		}
		bounds = new Rectangle2D.Double();
		bounds.setFrame(original.getBounds());
		centroid = (Point2D) original.getCentroid().clone();
		area = original.area;
		outlineMap = original.outlineMap;
	}

	/**
	 * Creates an SDPath representation of a general shape.
	 * 
	 * @see java.awt.geom.Shape
	 * @param shape
	 */
	public SDPath(final Shape shape) {
		Path2D.Double path = new Path2D.Double();
		path.append(shape, true);
		fromPath2D(path);
	}

	/**
	 * Sets the vertices of this SDPath to those of the Path2D instance.
	 * 
	 * @param path
	 */
	protected void fromPath2D(final Path2D path) {
		Path2DIterator iterator = new Path2DIterator(path);
		vertices = new ArrayList<Point2D>();
		while (iterator.hasNext()) {
			vertices.add(iterator.next());
		}
		/*
		 * The last vertex returned from SDPathIterator is also the first. The
		 * distance between the end and start points would then be 0, which
		 * causes failure of the periodic interpolator (the points must be
		 * strictly increasing). So we remove it.
		 */
		/*
		 * So all of the above seems irrelevant now, removing it causes problems
		 * with calculating the area of mask polygons. Removing it does not
		 * cause interpolation problems at this time.
		 */
		// if (vertices.size() > 0) {
		// vertices.remove(vertices.size() - 1);
		// }
		bounds = path.getBounds2D();
	}

	/**
	 * Gets a Path2D instance from this SDPath.
	 * 
	 * @return
	 */
	public Path2D.Double getPath2D() {
		if (path2D == null) {
			path2D = new Path2D.Double(Path2D.Double.WIND_EVEN_ODD);
			Iterator<Point2D> iterator = iterator();
			if (iterator.hasNext()) {
				Point2D vertex = iterator.next();
				path2D.moveTo(vertex.getX(), vertex.getY());
				// Point2D first = vertex;
				// Point2D last = vertex;

				// System.out.println("\n\nPath2D ***");

				while (iterator.hasNext()) {
					vertex = iterator.next();
					// if (vertex != last && vertex != first) {
					path2D.lineTo(vertex.getX(), vertex.getY());
					// System.out.println(vertex.getX() + "," + vertex.getY());
					// }
				}
				path2D.closePath();
			}
		}
		return path2D;
	}

	/**
	 * Gets a rectangle describing the boundaries of this path.
	 * 
	 * @return Boundary rectangle.
	 */
	public Rectangle2D getBounds() {
		if (bounds == null) {
			if (vertices.size() == 0) {
				return bounds = new Rectangle2D.Double();
			}
			Point2D v = vertices.get(0);
			double minX = v.getX();
			double maxX = v.getX();
			double minY = v.getY();
			double maxY = v.getY();

			for (Point2D vertex : vertices) {
				/* Gets bounding rectangle */
				if (vertex.getX() < minX) {
					minX = vertex.getX();
				} else if (vertex.getX() > maxX) {
					maxX = vertex.getX();
				}
				if (vertex.getY() < minY) {
					minY = vertex.getY();
				} else if (vertex.getY() > maxY) {
					maxY = vertex.getY();
				}
			}
			bounds = new Rectangle2D.Double(minX, minY, maxX - minX, maxY
					- minY);
		}
		return bounds;
	}

	/**
	 * Gets a list of vertices describing the path.
	 * 
	 * @return
	 */
	public List<Point2D> getVertices() {
		return vertices;
	}

	/**
	 * Gets an Area object containing this path.
	 * 
	 * @return
	 */
	public Area getAreaPolygon() {
		Path2D path = getPath2D();
		return new Area(path);
	}

	/**
	 * Gets the centroid (geometric centre). This formula only works for closed
	 * paths that do not intersect themselves.
	 * 
	 * @see https://en.wikipedia.org/wiki/Centroid, Centroid of polygon
	 * @return
	 */
	public Point2D getCentroid() {
		if (centroid == null) {
			double x = 0d;
			double y = 0d;

			Iterator<Point2D> iterator = iterator();
			if (vertices.size() > 0) {
				Point2D a = vertices.get(vertices.size() - 1);

				while (iterator.hasNext()) {
					Point2D b = iterator.next();
					double factor2 = (a.getX() * b.getY() - b.getX() * a.getY());
					x += (a.getX() + b.getX()) * factor2;
					y += (a.getY() + b.getY()) * factor2;
					a = b;
				}
			}

			area = getArea();
			x /= 6d * area;
			y /= 6d * area;

			centroid = new Point2D.Double(x, y);
			/*
			 * Cannot derive the centroid from the areaCells when shapes contain
			 * nested shapes.
			 */
			/*
			 * Gets the centre of the bounding rectangle. Not useful for shapes
			 * such as triangles.
			 */
			// getBounds();
			// centroid = new Point2D.Double(bounds.getCenterX(),
			// bounds.getCenterY());
		}
		return centroid;
	}

	/**
	 * Gets the length of the perimeter.
	 * 
	 * @return
	 */
	public double getPerimeter() {
		return getOutlineMap().getPerimeter();
	}

	/**
	 * Gets the outline map.
	 * 
	 * @return
	 */
	public OutlineMap getOutlineMap() {
		if (outlineMap == null) {
			outlineMap = new OutlineMap(this);
		}
		return outlineMap;
	}

	/**
	 * Gets the area enclosed by the path. This formula only works for closed
	 * paths that do not intersect themselves.
	 * <p>
	 * 
	 * @see http://www.mathopenref.com/coordpolygonarea.html
	 * 
	 * @return Area in pixels squared.
	 */
	public double getArea() {
		if (area == 0.0) {
			Iterator<Point2D> iterator = iterator();
			if (vertices.size() > 0) {
				Point2D a = vertices.get(vertices.size() - 1);

				while (iterator.hasNext()) {
					Point2D b = iterator.next();
					area += (a.getX() + b.getX()) * (a.getY() - b.getY());
					a = b;
				}
				/* Includes the last point along the path as well. */
				// Point2D b = iterator().next();
				// area += (a.getX() + b.getX()) * (a.getY() - b.getY());

				area = Math.abs(area / 2.0);
			}
		}
		return area;
	}

	@Override
	public VertexIterator iterator() {
		return new VertexIterator(vertices, VertexIterator.FORWARD);
	}

	/**
	 * Draws this path to the graphics object.
	 * 
	 * @param graphics
	 * @param outlineColour
	 * @param fillColour
	 */
	public void draw(final Graphics2D graphics, final Color outlineColour,
			final Color fillColour) {
		Path2D path = getPath2D();
		graphics.setColor(fillColour);
		graphics.fill(path);

		graphics.setColor(outlineColour);
		graphics.draw(path);
	}

	/**
	 * Moves this path (with regards to the center of its bounding rectangle) to
	 * the specified position.
	 * 
	 * @param x
	 * @param y
	 */
	public void move(final double x, final double y) {
		double x0 = bounds.getCenterX();
		double y0 = bounds.getCenterY();

		for (Point2D vertex : vertices) {
			vertex.setLocation(vertex.getX() + x - x0, vertex.getY() + y - y0);
		}
		getCentroid();
		centroid.setLocation(centroid.getX() + x - x0, centroid.getY() + y - y0);
		// Path2D path = getPath2D();
		// path.transform(AffineTransform.getTranslateInstance(x - x0, y - y0));
		// fromPath2D(path);
	}

	/**
	 * Gets the vertices that correspond to distances from the starting point
	 * that are listed in <code>indices</code>.
	 * 
	 * @param indices
	 * @return
	 */
	public List<Point2D> getVertices(final List<Double> indices) {
		OutlineMap outlineMap = getOutlineMap();
		int n = indices.size();
		List<Point2D> vertices = new ArrayList<Point2D>(n);

		for (int i = 0; i < n; i++) {
			Point2D vertex = outlineMap.getVertex(indices.get(i));
			if (vertex != null) {
				vertices.add(vertex);
			}
		}
		return vertices;
	}

	// /**
	// * Rotates the path anti-clockwise around its center by the specified
	// angle
	// * (in radians).
	// *
	// * @param theta
	// */
	// public void rotate(final double theta) {
	// /* Better to use the centre of gravity for this? */
	// double x = path.getBounds2D().getCenterX();
	// double y = path.getBounds2D().getCenterY();
	//
	// path.transform(AffineTransform.getTranslateInstance(-x, -y));
	// path.transform(AffineTransform.getRotateInstance(theta));
	// path.transform(AffineTransform.getTranslateInstance(x, y));
	// }
	//
	// /* Scales this path to the specified size.
	// *
	// * @param bounds
	// */
	// public void resize(final Rectangle2D bounds) {
	// Rectangle2D currentBounds = this.bounds;
	// double x = currentBounds.getWidth();
	// double y = currentBounds.getHeight();
	//
	// path.transform(AffineTransform.getScaleInstance(bounds.getHeight() / x,
	// bounds.getWidth() / y));
	// }

}