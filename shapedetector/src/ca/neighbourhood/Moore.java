package ca.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import exceptions.NullParameterException;

/**
 * Gathers all neighbouring cells within the square 3r*3r centered on (x,y),
 * that is its Moore neighbourhood.
 */
public class Moore extends CellNeighbourhood2D {
	protected final int r;

	public Moore(Lattice<Cell> lattice, int r) throws NullParameterException {
		super(lattice);
		this.r = r;
	}

	public List<Cell> gatherNeighbours(Cell cell) {
		int neighbourhoodSize = (2 * r + 1) * (2 * r + 1);
		List<Cell> neighbourhood = new ArrayList<Cell>(neighbourhoodSize);

		int[] coordinates = cell.getCoordinates();
		for (int i = coordinates[0] - r; i <= coordinates[0] + r; i++) {
			for (int j = coordinates[1] - r; j <= coordinates[1] + r; j++) {
				add(neighbourhood, lattice.getCell(i, j));
			}
		}
		return neighbourhood;
	}
}
