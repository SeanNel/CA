package ca.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * Gathers all neighbouring cells within the given radius.
 * <p>
 * Begin by assuming all cells are in the square 3r*3r centered on (x,y). Then
 * exclude the cells that are not inside the circle.
 * <p>
 * This method may give slightly better memory performance than the Moore
 * neighbourhood.
 * <p>
 * Another way to find these cells may be to iterate row for row and adjust the
 * y coordinate as a function of x.
 */
public class VanNeumann<V> extends CellNeighbourhood2D<V> {
	protected final int neighbourhoodSize;
	protected final int r;

	public VanNeumann(final Lattice<V> lattice, final int r) throws NullParameterException {
		super(lattice);
		this.r = r;
		neighbourhoodSize = (int) Math.ceil(Math.PI * r * r);
	}

	@Override
	public List<Cell<V>> gatherNeighbours(final Cell<V> cell) throws CAException {
		List<Cell<V>> neighbourhood = new ArrayList<Cell<V>>(neighbourhoodSize);

		int[] coordinates = cell.getCoordinates();
		for (int i = coordinates[0] - r; i < coordinates[0] + r; i++) {
			for (int j = coordinates[1] - r; j < coordinates[1] + r; j++) {
				if (((i - coordinates[0]) * (i - coordinates[0]))
						+ ((j - coordinates[1]) * (j - coordinates[1])) <= r
						* r) {
					add(neighbourhood, lattice.get(i, j));
				}
			}
		}
		return neighbourhood;
	}
}
