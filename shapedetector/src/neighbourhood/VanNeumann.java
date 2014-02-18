package neighbourhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.Cell;
import ca.Lattice2D;

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
public class VanNeumann<V> extends Neighbourhood2D<V> {
	protected final int neighbourhoodSize;
	protected final int r;
	protected final boolean includeSelf;

	public VanNeumann(final Lattice2D<V> lattice, final int r,
			boolean includeSelf) {
		super(lattice);
		this.r = r;
		this.includeSelf = includeSelf;
		int neighbourhoodSize = (2 * r + 1) * (2 * r + 1);
		if (!includeSelf) {
			neighbourhoodSize--;
		}
		this.neighbourhoodSize = neighbourhoodSize - 1;
	}

	@Override
	protected Collection<Cell> gatherNeighbours(final Cell cell)
			throws Exception {
		int[] coordinates = cell.getCoordinates();
		int x = coordinates[0];
		int y = coordinates[1];

		List<Cell> neighbourhood = new ArrayList<Cell>(neighbourhoodSize);

		for (int i = x - r; i < x + r; i++) {
			for (int j = y - r; j < y + r; j++) {
				if (((i - x) * (i - x)) + ((j - y) * (j - y)) <= r * r) {
					Cell c = lattice.getCell(i, j);
					if (includeSelf || c != cell) {
						add(neighbourhood, c);
					}
				}
			}
		}
		return neighbourhood;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof VanNeumann && ((VanNeumann<?>) obj).r == r);
	}
}
