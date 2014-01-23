package ca.shapedetector.path;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Maps the vertices of a path to the distance from the starting point, along
 * the path, to each vertex.
 * 
 * @author Sean
 */
public class OutlineMap {
	/** Outline vertices. */
	protected final List<Point2D> vertices;
	/**
	 * Maps the index of each index in the vertices list to its distance from
	 * starting point along the perimeter.
	 */
	protected final double[] vertexIndices;
	/** Length of the perimeter. */
	protected final double perimeter;

	/**
	 * Constructor.
	 * 
	 * @param path
	 */
	public OutlineMap(final SDPath path) {
		vertices = path.getVertices();
		int n = vertices.size();
		vertexIndices = new double[n];

		double perimeter = 0.0;
		if (vertices.size() > 0) {
			Point2D lastVertex = vertices.get(n - 1);
			int i = 0;
			for (Point2D vertex : vertices) {
				vertexIndices[i++] = perimeter;
				double distance = lastVertex.distance(vertex);
				perimeter += distance;
				lastVertex = vertex;
			}
		}
		this.perimeter = perimeter;
	}

	/**
	 * Gets the length of the perimeter.
	 * 
	 * @return
	 */
	public double getPerimeter() {
		return perimeter;
	}

	/**
	 * Gets the distance around the perimeter from the starting point to the
	 * vertex at index i.
	 * 
	 * @param i
	 * @return
	 */
	public double getDistance(final int i) {
		return vertexIndices[i];
	}

	/**
	 * Gets the distance around the perimeter from the starting point to the
	 * vertex.
	 * 
	 * @param i
	 * @return
	 */
	public double getDistance(final Point2D vertex) {
		for (int i = 0; i < vertices.size(); i++) {
			Point2D v = vertices.get(i);
			if (v.equals(vertex)) {
				return vertexIndices[i];
			}
		}
		throw new RuntimeException("Vertex not found");
	}

	/**
	 * Gets a vertex closest to the specified distance from the starting point.
	 * 
	 * @param d
	 * @return
	 */
	public Point2D getVertex(final double distance) {
		double d = distance;
		/* TODO: use formula, not loop to constrain d to the period */
		while (d < 0) {
			d += perimeter;
		}
		while (d > perimeter) {
			d -= perimeter;
		}
		/* TODO: This is a linear search. Binary search would be better */
		for (int i = 0; i < vertexIndices.length; i++) {
			if (vertexIndices[i] >= d) {
				return vertices.get(i);
			}
		}

		return null;

		/* Binary search still has bugs */
		// int index = searchIndex(vertexIndices, d, 0, vertexIndices.length -
		// 1);
		// return vertices.get(index);
	}

	/**
	 * Binary search for y such that indices[y] == (int) x
	 * 
	 * @param indices
	 * @param x
	 * @param start
	 * @param end
	 * @return
	 */
	protected static int searchIndex(final double[] indices, final double y,
			final int start, final int end) {
		int mid = (start + end) / 2;

		if (end - start <= 1 && y >= indices[mid] && y <= indices[end]) {
			return mid;
		} else if (y < indices[start] || y > indices[end]) {
			return -1;
		} else if (y < indices[mid]) {
			return searchIndex(indices, y, start, mid); // - 1
		} else {
			return searchIndex(indices, y, mid, end); // + 1
		}
	}

}
