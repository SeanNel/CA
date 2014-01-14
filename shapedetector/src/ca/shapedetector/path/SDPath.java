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

import ca.Cell;
import ca.shapedetector.blob.Blob;

/**
 * An abstraction layer for working with paths that describe shapes.
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

	public SDPath(Blob blob) {
		addCells(blob.getOutlineCells());
	}

	public SDPath() {
		vertices = new ArrayList<Point2D>();
	}

	/**
	 * Constructs a path from a list of cells describing the outline.
	 * 
	 * @param cells
	 * @return
	 */
	public void addCells(List<Cell> cells) {
		vertices = new ArrayList<Point2D>(cells.size());

		Iterator<Cell> cellIterator = cells.iterator();
		int[] coordinates = cellIterator.next().getCoordinates();
		Point2D vertex = new Point2D.Double(coordinates[0], coordinates[1]);
		vertices.add(vertex);

		while (cellIterator.hasNext()) {
			coordinates = cellIterator.next().getCoordinates();
			vertex = new Point2D.Double(coordinates[0], coordinates[1]);
			vertices.add(vertex);
		}

		outlineMap = null;
		area = 0.0;
	}

	public void addVertices(List<Point2D> vertices) {
		this.vertices = new ArrayList<Point2D>(vertices);

		outlineMap = null;
		area = 0.0;
	}

	/**
	 * Copy constructor.
	 * 
	 * @param original
	 */
	public SDPath(SDPath original) {
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
	public SDPath(Shape shape) {
		Path2D.Double path = new Path2D.Double();
		path.append(shape, true);
		fromPath2D(path);
	}

	/**
	 * Sets the vertices of this SDPath to those of the Path2D instance.
	 * 
	 * @param path
	 */
	protected void fromPath2D(Path2D path) {
		SDPathIterator iterator = new SDPathIterator(path);
		vertices = new ArrayList<Point2D>();
		while (iterator.hasNext()) {
			vertices.add(iterator.next());
		}
		bounds = path.getBounds2D();
		centroid = new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
	}

	/**
	 * Gets a Path2D instance from this SDPath.
	 * 
	 * @return
	 */
	public Path2D.Double getPath2D() {
		if (path2D == null) {
			path2D = new Path2D.Double();
			Iterator<Point2D> iterator = iterator();
			if (iterator.hasNext()) {
				Point2D vertex = iterator.next();
				path2D.moveTo(vertex.getX(), vertex.getY());

				while (iterator.hasNext()) {
					vertex = iterator.next();
					path2D.lineTo(vertex.getX(), vertex.getY());
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

	public double getCentreX() {
		return bounds.getCenterX();
	}

	public double getCentreY() {
		return bounds.getCenterY();
	}

	/**
	 * Gets an array of points on the outline of the shape.
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
	 * Gets the centroid (centre of gravity).
	 * 
	 * @return
	 */
	public Point2D getCentroid() {
		if (centroid == null) {
			/*
			 * Cannot derive the centroid from the areaCells when shapes contain
			 * nested shapes. So for now, the centre of gravity is the same as
			 * the geometric centre. This only becomes an issue once we test for
			 * non-symmetrical shapes.
			 */
			getBounds();
			centroid = new Point2D.Double(bounds.getCenterX(),
					bounds.getCenterY());
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
	 * Gets the area enclosed by the path. Formula source:
	 * http://www.mathopenref.com/coordpolygonarea.html
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
				// iterator2 = iterator();
				//
				// Point2D a = iterator1.next();
				// Point2D b = iterator2.next();
				// area += a.getX() * b.getY() - a.getY() * b.getX();

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
	public void draw(Graphics2D graphics, Color outlineColour, Color fillColour) {
		Path2D path = getPath2D();
		graphics.setColor(fillColour);
		graphics.fill(path);

		graphics.setColor(outlineColour);
		graphics.draw(path);
	}

	/**
	 * Moves this path (with regards to its center) to the specified position.
	 * 
	 * @param x
	 * @param y
	 */
	public void move(double x, double y) {
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

	// /**
	// * Rotates the path anti-clockwise around its center by the specified
	// angle
	// * (in radians).
	// *
	// * @param theta
	// */
	// public void rotate(double theta) {
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
	// public void resize(Rectangle2D bounds) {
	// Rectangle2D currentBounds = this.bounds;
	// double x = currentBounds.getWidth();
	// double y = currentBounds.getHeight();
	//
	// path.transform(AffineTransform.getScaleInstance(bounds.getHeight() / x,
	// bounds.getWidth() / y));
	// }

}