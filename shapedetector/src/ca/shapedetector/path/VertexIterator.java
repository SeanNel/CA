package ca.shapedetector.path;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

/**
 * Iterates along the vertices of a path.
 * 
 * @author Sean
 */
public class VertexIterator implements Iterator<Point2D> {
	public final static int FORWARD = 0;
	public final static int REVERSE = 1;
	
	protected final int direction;
	protected final List<Point2D> vertices;
	protected int index;

	/**
	 * Constructor.
	 * 
	 * @param vertices
	 * @param direction
	 */
	public VertexIterator(final List<Point2D> vertices, final int direction) {
		this.vertices = vertices;
		this.direction = direction;
		if (direction == FORWARD) {
			index = 0;
		} else {
			index = vertices.size() - 1;
		}
	}

	@Override
	public boolean hasNext() {
		if (direction == FORWARD) {
			return index < vertices.size();
		} else {
			return index > 0;
		}
	}

	@Override
	public Point2D next() {
		if (direction == FORWARD) {
			return vertices.get(index++);
		} else {
			return vertices.get(index--);
		}
	}

	@Override
	public void remove() {
		vertices.remove(vertices.get(index));
	}
}
