package path;

import java.awt.geom.Point2D;
import java.util.Iterator;
import java.util.List;

/**
 * Iterates along the vertices of a path.
 * 
 * @author Sean
 */
public class VertexIterator implements Iterator<Point2D> {
	public enum Direction {
		FORWARD, REVERSE;
	}

	protected Direction direction;
	protected final List<Point2D> vertices;
	protected int index;
	protected boolean continuous;

	/**
	 * Constructor.
	 * 
	 * @param vertices
	 * @param direction
	 */
	public VertexIterator(final List<Point2D> vertices,
			final Direction direction, final boolean continuous) {
		this.vertices = vertices;
		this.direction = direction;
		if (direction == Direction.FORWARD) {
			index = 0;
		} else {
			index = vertices.size() - 1;
		}
		this.continuous = continuous;
	}

	@Override
	public boolean hasNext() {
		if (continuous && vertices.size() > 0) {
			return true;
		} else if (direction == Direction.FORWARD) {
			return index < vertices.size();
		} else {
			return index > 0;
		}
	}

	@Override
	public Point2D next() {
		checkLimits();
		if (direction == Direction.FORWARD) {
			return vertices.get(index++);
		} else {
			return vertices.get(index--);
		}
	}

	@Override
	public void remove() {
		vertices.remove(vertices.get(index));
	}

	public void setDirection(final Direction direction) {
		this.direction = direction;
		checkLimits();
	}

	private void checkLimits() {
		if (continuous && index >= vertices.size()) {
			index = 0;
		} else if (continuous && index < 0) {
			index = vertices.size() - 1;
		}
	}

	public void setContinuity(final boolean continuous) {
		this.continuous = continuous;
		checkLimits();
	}

	public VertexIterator clone() {
		VertexIterator iterator = new VertexIterator(vertices, direction,
				continuous);
		iterator.index = index;
		return iterator;
	}
}
