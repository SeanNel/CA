package ca;

import java.lang.reflect.Array;
import java.util.Iterator;

import neighbourhood.Neighbourhood;

import rules.Rule;

public class Lattice1D<V> extends AbstractLattice<V> {
	/** One dimensional array of cells. */
	protected Cell[] cellLattice;

	/** Two dimensional array of states before rule application. */
	protected V[] statesBefore;
	/** Two dimensional array of states after rule application. */
	protected V[] statesAfter;

	protected Neighbourhood neighbourhood;

	@SuppressWarnings("unchecked")
	public Lattice1D(Class<V> componentType, int w) throws Exception {
		cellLattice = new Cell[w];
		initLattice(w);
		statesBefore = (V[]) Array.newInstance(componentType, w);
		statesAfter = (V[]) Array.newInstance(componentType, w);
	}

	protected void initLattice(int w) {
		for (int x = 0; x < w; x++) {
			cellLattice[x] = new Cell(x);
		}
	}

	@Override
	public void apply(Iterable<Rule<Cell>> rules) throws Exception {
		apply(this, rules);
	}

	protected void complete() {
		statesBefore = statesAfter;
		statesAfter = statesAfter.clone();
	}

	/**
	 * Gets the cell corresponding to (x,y) in the source image.
	 * <p>
	 * Returns null when coordinates are out of bounds.
	 * 
	 * @param x
	 *            Cell's x-coordinate.
	 * @param y
	 *            Cell's y-coordinate.
	 * @return Cell at the specified position.
	 * @throws Exception
	 */
	@Override
	public Cell getCell(final int... x) throws Exception {
		if (x.length != 1) {
			throw new Exception("Wrong number of dimensions");
		}
		int[] dimensions = { cellLattice.length };
		if (x[0] >= 0 && x[1] >= 0 && x[0] < dimensions[0]
				&& x[1] < dimensions[1]) {
			return cellLattice[x[0]];
		} else {
			return null;
		}
	}

	/**
	 * Gets the state of of the cell.
	 * 
	 * @param cell
	 * @return
	 * @throws Exception
	 */
	public V getState(final Cell cell) throws Exception {
		return statesBefore[cell.getCoordinates()[0]];
	}

	/**
	 * Sets the state of the cell.
	 * 
	 * @param cell
	 * @param state
	 * @throws Exception
	 */
	public void setState(final Cell cell, final V state) throws Exception {
		statesAfter[cell.getCoordinates()[0]] = state;
	}

	/**
	 * Gets the lattice width.
	 * 
	 * @return Lattice width.
	 */
	public int getLength() {
		return cellLattice.length;
	}

	@Override
	public Iterator<Cell> iterator() {
		return new Iterator<Cell>() {
			private int i = 0;

			@Override
			public boolean hasNext() {
				return i < cellLattice.length;
			}

			@Override
			public Cell next() {
				return cellLattice[i++];
			}

			@Override
			public void remove() {
				throw new RuntimeException("remove not allowed");
			}
		};
	}
}
