package neighbourhood;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import ca.Cell;
import ca.Lattice2D;

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
public class MooreOutline<V> extends Moore<V> {

	public MooreOutline(final Lattice2D<V> lattice) {
		super(lattice, 1, false);
	}

	@Override
	protected Collection<Cell> gatherNeighbours(final Cell cell)
			throws Exception {
		int[] coordinates = cell.getCoordinates();
		int x = coordinates[0];
		int y = coordinates[1];

		List<Cell> neighbourhood = new ArrayList<Cell>(8);

		add(neighbourhood, lattice.getCell(x, y - 1));
		add(neighbourhood, lattice.getCell(x + 1, y - 1));
		add(neighbourhood, lattice.getCell(x + 1, y));
		add(neighbourhood, lattice.getCell(x + 1, y + 1));
		add(neighbourhood, lattice.getCell(x, y + 1));
		add(neighbourhood, lattice.getCell(x - 1, y + 1));
		add(neighbourhood, lattice.getCell(x - 1, y));
		add(neighbourhood, lattice.getCell(x - 1, y - 1));

		return neighbourhood;
	}

	@Override
	public boolean equals(Object obj) {
		return (obj instanceof MooreOutline);
	}
}
