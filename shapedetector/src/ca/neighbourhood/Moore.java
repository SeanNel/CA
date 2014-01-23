package ca.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * Gathers all neighbouring cells within the square 3r*3r centered on (x,y),
 * that is its Moore neighbourhood.
 */
public class Moore<V> extends CellNeighbourhood2D<V> {
	protected final int r;

	public Moore(final Lattice<V> lattice, final int r)
			throws NullParameterException {
		super(lattice);
		this.r = r;
	}

	@Override
	public List<Cell<V>> gatherNeighbours(final Cell<V> cell)
			throws CAException {
		int neighbourhoodSize = (2 * r + 1) * (2 * r + 1);
		List<Cell<V>> neighbourhood = new ArrayList<Cell<V>>(neighbourhoodSize);

		int[] coordinates = cell.getCoordinates();
		for (int i = coordinates[0] - r; i <= coordinates[0] + r; i++) {
			for (int j = coordinates[1] - r; j <= coordinates[1] + r; j++) {
				add(neighbourhood, lattice.get(i, j));
			}
		}
		return neighbourhood;
	}
}
