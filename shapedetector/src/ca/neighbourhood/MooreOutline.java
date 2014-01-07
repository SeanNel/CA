package ca.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import exceptions.NullParameterException;

/**
 * Gathers the specified cell's Moore neighbourhood with r=1, not including the
 * current cell. Places cells in anti-clockwise order, starting with the cell
 * directly above this one. The sequence determines how outline cells will be
 * ordered.
 * <p>
 * NOTE: the order has been made clockwise until the issue with reverse
 * PathIterators has been resolved.
 */
public class MooreOutline extends Neighbourhood2D {

	public MooreOutline(Lattice lattice) throws NullParameterException {
		super(lattice);
	}

	public List<Cell> gatherNeighbours(Cell cell) {
		int[] coordinates = cell.getCoordinates();
		int x = coordinates[0];
		int y = coordinates[1];
		List<Cell> neighbourhood = new ArrayList<Cell>(8);
		// add(neighbourhood, getCell(x, y));

		// add(neighbourhood, ca.getCell(x - 1, y - 1));
		// add(neighbourhood, (ca.getCell(x - 1, y));
		// add(neighbourhood, ca.getCell(x - 1, y + 1));
		// add(neighbourhood, ca.getCell(x, y + 1));
		// add(neighbourhood, ca.getCell(x + 1, y + 1));
		// add(neighbourhood, ca.getCell(x + 1, y));
		// add(neighbourhood, ca.getCell(x + 1, y - 1));
		// add(neighbourhood, ca.getCell(x, y - 1));

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
}
