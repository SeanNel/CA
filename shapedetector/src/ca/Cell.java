package ca;

/**
 * The operational unit of a cellular automaton. Simply stores its own
 * coordinates, so that its neighbours can be found.
 * 
 * @author Sean
 */
public class Cell {
	/** The cell's position coordinates. */
	protected final int[] coordinates;

	/**
	 * Constructor.
	 */
	public Cell(final int... coordinates) {
		this.coordinates = coordinates;
	}

	/**
	 * Gets this cell's coordinates.
	 * 
	 * @return This cell's coordinates.
	 */
	public int[] getCoordinates() {
		return coordinates;
	}

	public String toString() {
		/* Assumes cell is in 2D lattice */
		return "(Cell) [x=" + coordinates[0] + ", y=" + coordinates[1] + "]";
	}
}