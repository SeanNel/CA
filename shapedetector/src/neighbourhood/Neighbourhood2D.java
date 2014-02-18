package neighbourhood;

import java.util.Collection;

import ca.Cell;
import ca.Lattice2D;

/**
 * Finds a cell's neighbours and caches the results to speed up future queries.
 * <p>
 * The current implementation assumes that the neighbourhood will only be used
 * with one lattice instance.
 * 
 * @author Sean
 */
public abstract class Neighbourhood2D<V> implements Neighbourhood {
	/** Two dimensional array of cell neighbourhoods. */
	private Collection<Cell>[][] neighbourhoods;
	protected boolean shouldCache;
	protected final Lattice2D<V> lattice;

	/**
	 * Creates a lattice neighbourhood and caches the results.
	 * 
	 * @param lattice
	 */
	public Neighbourhood2D(final Lattice2D<V> lattice) {
		this(lattice, true);
	}

	@SuppressWarnings("unchecked")
	public Neighbourhood2D(final Lattice2D<V> lattice, final boolean shouldCache) {
		if (lattice == null) {
			throw new NullPointerException();
		}
		this.lattice = lattice;
		this.shouldCache = shouldCache;
		if (shouldCache) {
			neighbourhoods = (Collection<Cell>[][]) new Collection[lattice
					.getWidth()][lattice.getHeight()];
		}
	}

	@Override
	public final Collection<Cell> neighbours(final Cell cell) throws Exception {
		Collection<Cell> cellNeighbourhood;
		if (shouldCache) {
			int[] x = cell.getCoordinates();
			cellNeighbourhood = neighbourhoods[x[0]][x[1]];

			if (cellNeighbourhood == null) {
				cellNeighbourhood = gatherNeighbours(cell);
				neighbourhoods[x[0]][x[1]] = cellNeighbourhood;
			}
		} else {
			cellNeighbourhood = gatherNeighbours(cell);
		}
		return cellNeighbourhood;
	}

	protected Collection<Cell> gatherNeighbours(final Cell cell)
			throws Exception {
		throw new Exception("Method not implemented.");
	}

	@SuppressWarnings("unchecked")
	@Override
	public void clear() {
		neighbourhoods = (Collection<Cell>[][]) new Collection[neighbourhoods.length][neighbourhoods[0].length];
	}

	protected void add(Collection<Cell> neighbourhood, Cell cell) {
		if (cell != null) {
			neighbourhood.add(cell);
		}
	}
}
