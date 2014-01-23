package ca.neighbourhood;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * Gets the neighbouring cells, above, below, left and right of the specified
 * cell. Optimized for VanNeumann neighbourhood, r=1. Does not include the cell
 * in its own neighbourhood.
 */
public class VanNeumannCardinal<V> extends CellNeighbourhood2D<V> {

	public VanNeumannCardinal(final Lattice<V> lattice) throws NullParameterException {
		super(lattice);
	}

	@Override
	public List<Cell<V>> gatherNeighbours(final Cell<V> cell) throws CAException {
		int[] coordinates = cell.getCoordinates();
		List<Cell<V>> neighbourhood = new ArrayList<Cell<V>>(4);
		// neighbourhood.add(getCell(coordinates[0], coordinates[1]));
		add(neighbourhood, lattice.get(coordinates[0], coordinates[1] - 1));
		add(neighbourhood, lattice.get(coordinates[0], coordinates[1] + 1));
		add(neighbourhood, lattice.get(coordinates[0] - 1, coordinates[1]));
		add(neighbourhood, lattice.get(coordinates[0] + 1, coordinates[1]));
		return neighbourhood;
	}
}
