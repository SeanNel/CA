package neighbourhood;

import java.util.ArrayList;
import java.util.Collection;

import ca.Cell;
import ca.Lattice2D;

/**
 * Gathers all neighbouring cells within the square 3r*3r centered on (x,y),
 * that is its Moore neighbourhood.
 */
public class Moore<V> extends Neighbourhood2D<V> {
	protected final int neighbourhoodSize;
	protected final int r;
	protected final boolean includeSelf;

	public Moore(final Lattice2D<V> lattice, final int r, boolean includeSelf) {
		super(lattice);
		this.r = r;
		this.includeSelf = includeSelf;
		int neighbourhoodSize = (2 * r + 1) * (2 * r + 1);
		if (!includeSelf) {
			neighbourhoodSize--;
		}
		this.neighbourhoodSize = neighbourhoodSize;
	}

	@Override
	protected Collection<Cell> gatherNeighbours(final Cell cell)
			throws Exception {
		Collection<Cell> neighbourhood = new ArrayList<Cell>(neighbourhoodSize);

		int[] coordinates = cell.getCoordinates();
		int x = coordinates[0];
		int y = coordinates[1];

		for (int i = x - r; i <= x + r; i++) {
			for (int j = y - r; j <= y + r; j++) {
				Cell c = lattice.getCell(i, j);
				if (includeSelf || c != cell) {
					add(neighbourhood, c);
				}
			}
		}

		return neighbourhood;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof Moore && ((Moore<?>) obj).r == r);
	}
}
