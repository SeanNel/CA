package path;

import java.awt.geom.Point2D;
import java.util.List;

/**
 * Iterates along the vertices of a path, interpolating points along a straight
 * line between vertices if the distance between them is greater than the step
 * length.
 * 
 * @author Sean
 */
public class InterpolatingVertexIterator extends VertexIterator {
	protected final double step;
	protected Point2D currentPosition;

	public InterpolatingVertexIterator(final List<Point2D> vertices,
			final Direction direction, final double step,
			final boolean continuous) {
		super(vertices, direction, continuous);
		this.step = step;
		if (direction == Direction.FORWARD) {
			currentPosition = vertices.get(vertices.size() - 1);
		} else {
			currentPosition = vertices.get(0);
		}
	}

	@Override
	public Point2D next() {
		if (continuous) {
			// TODO
		}

		Point2D nextPosition = vertices.get(index);

		/* Interpolates points between vertices further apart than step length. */
		double x = nextPosition.getX() - currentPosition.getX();
		double y = nextPosition.getY() - currentPosition.getY();

		double modulus = Math.sqrt(x * x + y * y);

		if (modulus > step) {
			x = currentPosition.getX() + (x / modulus * step);
			y = currentPosition.getY() + (y / modulus * step);
			nextPosition = new Point2D.Double(x, y);
		} else if (direction == Direction.FORWARD) {
			index++;
		} else {
			index--;
		}

		return currentPosition = nextPosition;
	}

	@Override
	public void remove() {
		vertices.remove(vertices.get(index));
	}
}
