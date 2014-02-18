package neighbourhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.Cell;
import ca.Lattice2D;

/**
 * Gets the neighbouring cells, above, below, left and right of the specified
 * cell. Optimized for VanNeumann neighbourhood, r=1. Does not include the cell
 * in its own neighbourhood.
 */
public class VanNeumannCardinal<V> extends VanNeumann<V> {

	public VanNeumannCardinal(final Lattice2D<V> lattice) {
		super(lattice, 1, false);
	}

	@Override
	protected Collection<Cell> gatherNeighbours(final Cell cell)
			throws Exception {
		int[] coordinates = cell.getCoordinates();
		int x = coordinates[0];
		int y = coordinates[1];

		List<Cell> neighbourhood = new ArrayList<Cell>(4);
		// neighbourhood.add(new int[](x, coordinates
		// .get(1)));
		add(neighbourhood, lattice.getCell(x, y - 1));
		add(neighbourhood, lattice.getCell(x, y + 1));
		add(neighbourhood, lattice.getCell(x - 1, y));
		add(neighbourhood, lattice.getCell(x + 1, y));

		return neighbourhood;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof VanNeumannCardinal);
	}
}
