package ca;

import rules.Rule;

/**
 * A lattice of cells.
 * 
 * @author Sean
 * 
 * @param <V>
 */
public interface Lattice<V> extends Iterable<Cell> {
	/**
	 * Applies the rules to each cell in the lattice.
	 * 
	 * @param rules
	 * @throws Exception
	 */
	public void apply(final Iterable<Rule<Cell>> rules) throws Exception;

	/**
	 * Applies the rules to the specified cells in the lattice.
	 * 
	 * @param rules
	 * @throws Exception
	 */
	public void apply(final Iterable<Cell> cells,
			final Iterable<Rule<Cell>> rules) throws Exception;

	/**
	 * Gets the cell at the specified coordinates.
	 * <p>
	 * Returns the paddingCell when coordinates are out of bounds.
	 * 
	 * @return Cell at the specified position.
	 * @throws Exception
	 * @throws CAException
	 *             if the parameters do not match the lattice dimensions.
	 */
	public Cell getCell(final int... x) throws Exception;

	/**
	 * Gets the state of the specified cell.
	 * 
	 * @param cell
	 * @throws Exception
	 */
	public V getState(final Cell cell) throws Exception;

	/**
	 * Sets the state of the specified cell.
	 * 
	 * @param cell
	 * @param state
	 * @throws Exception
	 */
	public void setState(final Cell cell, final V state) throws Exception;
}
