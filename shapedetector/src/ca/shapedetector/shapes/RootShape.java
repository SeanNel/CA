package ca.shapedetector.shapes;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.Cell;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.path.SDPath;
import exceptions.NullParameterException;

public class RootShape<V> extends AbstractShape {
	protected final List<AbstractShape> shapeClasses;

	public RootShape() {
		super(null, null, 0d);
		shapeClasses = new ArrayList<AbstractShape>();
		shapeClasses.add(new Ellipse());
		shapeClasses.add(new Triangle());
		shapeClasses.add(new Quadrilateral());
	}

	/**
	 * Returns an instance of the shape detected from the blob. Returns an
	 * UnknownShape if none was found.
	 * 
	 * @param blob
	 * @return
	 * @throws NullParameterException 
	 */
	public AbstractShape identify(final Blob<V> blob) throws NullParameterException {
		SDPath path = path(blob.getOutlineCells());
		AbstractShape shape = new UnknownShape(path);

		for (SDShape relatedShape : shapeClasses) {
			shape = relatedShape.identify(shape);
			if (!(shape instanceof UnknownShape)) {
				break;
			}
		}
		return shape;
	}

	@Override
	public AbstractShape identify(AbstractShape shape) {
		return null;
	}

	/**
	 * Constructs a path from a list of cells describing the outline.
	 * 
	 * @param cells
	 * @return
	 */
	public SDPath path(List<Cell<V>> cells) {
		ArrayList<Point2D> vertices = new ArrayList<Point2D>(cells.size());

		Iterator<Cell<V>> cellIterator = cells.iterator();
		if (cellIterator.hasNext()) {
			int[] coordinates = cellIterator.next().getCoordinates();
			Point2D vertex = new Point2D.Double(coordinates[0], coordinates[1]);
			vertices.add(vertex);

			while (cellIterator.hasNext()) {
				coordinates = cellIterator.next().getCoordinates();
				vertex = new Point2D.Double(coordinates[0], coordinates[1]);
				vertices.add(vertex);
			}
		}

		SDPath path = new SDPath();
		path.addVertices(vertices);
		return path;
	}
}