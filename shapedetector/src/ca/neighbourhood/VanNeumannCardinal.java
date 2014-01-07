package ca.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import exceptions.NullParameterException;

/**
 * Caches the neighbouring cells of the specified cell. Optimized for VanNeumann
 * neighbourhood, r=1. Does not include the cell in its own neighbourhood.
 */
public class VanNeumannCardinal extends Neighbourhood2D {

	public VanNeumannCardinal(Lattice lattice) throws NullParameterException {
		super(lattice);
	}

	public List<Cell> gatherNeighbours(Cell cell) {
		int[] coordinates = cell.getCoordinates();
		List<Cell> neighbourhood = new ArrayList<Cell>(4);
		// neighbourhood.add(getCell(coordinates[0], coordinates[1]));
		add(neighbourhood, lattice.getCell(coordinates[0], coordinates[1] - 1));
		add(neighbourhood, lattice.getCell(coordinates[0], coordinates[1] + 1));
		add(neighbourhood, lattice.getCell(coordinates[0] - 1, coordinates[1]));
		add(neighbourhood, lattice.getCell(coordinates[0] + 1, coordinates[1]));
		return neighbourhood;
	}
}
