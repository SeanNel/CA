package ca.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.shapedetector.BlobMap;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * Gathers the specified cell's Moore neighbourhood with r=1, not including the
 * current cell. Places cells in anti-clockwise order, starting with the cell
 * directly above this one. The sequence determines how outline cells will be
 * ordered.
 * <p>
 * NOTE: the order has been made clockwise until the issue with reverse
 * PathIterators has been resolved. Does not really make a difference at this
 * point.
 */
public class MooreOutline<V> extends CellNeighbourhood2D<V> {
	protected final BlobMap<V> blobMap;

	public MooreOutline(final Lattice<V> lattice, final BlobMap<V> blobMap)
			throws NullParameterException {
		super(lattice);
		this.blobMap = blobMap;
	}

	@Override
	public List<Cell<V>> gatherNeighbours(final Cell<V> cell)
			throws CAException {
		int[] coordinates = cell.getCoordinates();
		int x = coordinates[0];
		int y = coordinates[1];
		List<Cell<V>> neighbourhood = new ArrayList<Cell<V>>(8);

		add(neighbourhood, cell, lattice.get(x, y - 1));
		add(neighbourhood, cell, lattice.get(x + 1, y - 1));
		add(neighbourhood, cell, lattice.get(x + 1, y));
		add(neighbourhood, cell, lattice.get(x + 1, y + 1));
		add(neighbourhood, cell, lattice.get(x, y + 1));
		add(neighbourhood, cell, lattice.get(x - 1, y + 1));
		add(neighbourhood, cell, lattice.get(x - 1, y));
		add(neighbourhood, cell, lattice.get(x - 1, y - 1));

		return neighbourhood;
	}

	/**
	 * Only adds the neighbour if it belongs to the same blob.
	 * 
	 * @param neighbourhood
	 * @param cell
	 * @param neighbour
	 * @throws CAException
	 */
	protected void add(final List<Cell<V>> neighbourhood, final Cell<V> cell,
			final Cell<V> neighbour) throws CAException {
		if (cell != null && blobMap.getBlob(cell) == blobMap.getBlob(neighbour)) {
			neighbourhood.add(neighbour);
		}
	}
}
