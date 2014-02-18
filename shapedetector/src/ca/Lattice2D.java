package ca;

import java.lang.reflect.Array;
import java.util.Iterator;

import rules.Rule;

public class Lattice2D<V> extends AbstractLattice<V> {
	/** Two dimensional array of cells. */
	protected Cell[][] cellLattice;

	/** Two dimensional array of states before rule application. */
	protected V[][] statesBefore;
	/** Two dimensional array of states after rule application. */
	protected V[][] statesAfter;

	protected final Class<V> componentType;

	/**
	 * Constructor. Note that due to a weakness with the Java generic
	 * implementation, we also need the parameter E as componentType to create
	 * generic arrays.
	 * 
	 * @param componentType
	 * @param w
	 * @param h
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Lattice2D(Class<V> componentType, int w, int h) throws Exception {
		cellLattice = new Cell[w][h];
		for (int x = 0; x < w; x++) {
			for (int y = 0; y < h; y++) {
				cellLattice[x][y] = new Cell(new int[] { x, y });
			}
		}
		statesBefore = (V[][]) Array.newInstance(componentType, w, h);
		statesAfter = (V[][]) Array.newInstance(componentType, w, h);
		this.componentType = componentType;
	}

	@Override
	public void apply(Iterable<Rule<Cell>> rules) throws Exception {
		apply(this, rules);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void complete() {
		statesBefore = statesAfter;

		// statesAfter = statesAfter.clone();
		// Proper clone:
		statesAfter = (V[][]) Array.newInstance(componentType,
				statesAfter.length, statesAfter[0].length);
		for (int i = 0; i < statesAfter.length; i++) {
			for (int j = 0; j < statesAfter[0].length; j++) {
				statesAfter[i][j] = statesBefore[i][j];
			}
		}
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
		if (x.length != 2) {
			throw new Exception("Wrong number of dimensions");
		}
		int i = x[0];
		int j = x[1];

		if (i >= 0 && j >= 0 && i < cellLattice.length
				&& j < cellLattice[0].length) {
			return cellLattice[i][j];
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
		if (cell == null) {
			throw new NullPointerException();
		}
		int[] x = cell.getCoordinates();
		return statesBefore[x[0]][x[1]];
	}

	/**
	 * Sets the state of the cell.
	 * 
	 * @param cell
	 * @param state
	 * @throws Exception
	 */
	public void setState(final Cell cell, final V state) throws Exception {
		int[] x = cell.getCoordinates();
		statesAfter[x[0]][x[1]] = state;
	}

	/**
	 * Gets the lattice width.
	 * 
	 * @return Lattice width.
	 */
	public int getWidth() {
		return cellLattice.length;
	}

	/**
	 * Gets the lattice height.
	 * 
	 * @return Lattice height.
	 */
	public int getHeight() {
		return cellLattice[0].length;
	}

	@Override
	public Iterator<Cell> iterator() {
		return new Iterator<Cell>() {
			private int numRows = cellLattice.length;
			private int numCols = cellLattice[0].length;

			private int x = 0;
			private int y = 0;

			@Override
			public boolean hasNext() {
				return x < numRows && y < numCols;
			}

			@Override
			public Cell next() {
				Cell next = cellLattice[x++][y];
				/*
				 * It is possible to lazy load the cells from here, but it
				 * doesn't seem any more efficient.
				 */
				if (x >= cellLattice.length) {
					x = 0;
					y++;
				}
				return next;
			}

			@Override
			public void remove() {
				throw new RuntimeException("remove not allowed");
			}
		};
	}
}
