package ca.rules.blob;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.shapedetector.BlobMap;
import ca.shapedetector.ShapeList;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.blob.LoopFinder;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.AbstractShape;
import ca.shapedetector.shapes.RootShape;
import ca.shapedetector.shapes.UnknownShape;
import exceptions.CAException;
import graphics.SDPanelTheme;

/**
 * Places outline cells in clockwise sequence, with pseudo-random starting
 * position along the shape's top boundary (depending on which cell was added to
 * the list first).
 * <p>
 * Notice that this forms a closed loop of the shape's outside, so that it
 * automatically disregards any other enveloped shapes. This does not however,
 * do anything to add those shapes' areaCells to the shape enveloping them.
 */
public class ArrangeOutlineRule<V> extends BlobRule<V> {
	protected final RootShape<V> shapeDetector = new RootShape<V>();
	protected final Lattice<V> lattice;
	protected final ShapeList shapeList;

	public ArrangeOutlineRule(final BlobMap<V> blobMap,
			final ShapeList shapeList, final Lattice<V> lattice) {
		super(blobMap);
		this.shapeList = shapeList;
		this.lattice = lattice;
	}

	@Override
	public void update(final Blob<V> blob) throws CAException {

		List<Cell<V>> cells = blob.getOutlineCells();
		Cell<V> first = firstOutlineCell(cells, blob.getBounds());

		// System.out.println("Unarranged outline cells:");
		// System.out.println(cells);

		if (first != null && cells.size() > 16) {
			// /* For debugging */
			// if (ShapeDetector.debug) {
			// display(cells);
			// }

			LoopFinder<V> loopFinder = new LoopFinder<V>(cells, lattice,
					blobMap);
			cells = loopFinder.getLoop(first);
			blob.setOutlineCells(cells);

			// /* For debugging */
			// if (ShapeDetector.debug) {
			// display(cells);
			// }
		}

		// System.out.println("Arranged outline cells:");
		// System.out.println(cells);
	}

	/**
	 * Finds a cell along the top boundary.
	 * <p>
	 * It is not difficult to ensure that the top-left cell is selected, but any
	 * one at the top boundary will work, so we'll just pick one, because it's
	 * faster that way.
	 * 
	 * @param cells
	 * @param bounds
	 * @return The '1st' cell to start the loop of outline cells.
	 */
	protected Cell<V> firstOutlineCell(final List<Cell<V>> cells,
			final Rectangle2D bounds) {
		for (Cell<V> cell : cells) {
			int[] coordinates = cell.getCoordinates();
			/* Looks at row along top boundary: */
			if (coordinates[1] == bounds.getMinY()) {
				return cell;
			}
		}
		return null;
	}

	/**
	 * For debugging. Displays a blob made of the specified cells.
	 * 
	 * @param cells
	 */
	public void display(final List<Cell<V>> cells) {
		graphics.ShapeFrame.setTheme(SDPanelTheme.DEFAULT);
		SDPath path = getPath(cells);
		AbstractShape shape = new UnknownShape(path);
		graphics.ShapeFrame.reset(shape);
		graphics.ShapeFrame.display(shape);
	}

	/**
	 * Constructs a path from a list of cells describing the outline. Used in
	 * the debug method above.
	 * 
	 * @param cells
	 * @return
	 */
	public SDPath getPath(final List<Cell<V>> cells) {
		List<Point2D> vertices = new ArrayList<Point2D>(cells.size());

		Iterator<Cell<V>> cellIterator = cells.iterator();
		int[] coordinates = cellIterator.next().getCoordinates();
		Point2D vertex = new Point2D.Double(coordinates[0], coordinates[1]);
		vertices.add(vertex);

		while (cellIterator.hasNext()) {
			coordinates = cellIterator.next().getCoordinates();
			vertex = new Point2D.Double(coordinates[0], coordinates[1]);
			vertices.add(vertex);
		}
		return new SDPath(vertices);
	}
}
