package rules.cell;

import neighbourhood.Neighbourhood;
import rules.AbstractRule;
import ca.Cell;
import ca.Lattice;

/**
 * Causes neighbourhoods to be cached for each cell in the lattice.
 */
public class CacheNeighbourhoodRule<V> extends AbstractRule<Cell> {
	protected final Lattice<V> lattice;
	protected final Neighbourhood neighbourhood;

	public CacheNeighbourhoodRule(final Lattice<V> lattice,
			final Neighbourhood neighbourhood) {
		super();
		if (lattice == null) {
			throw new NullPointerException("doubleLattice");
		}
		if (neighbourhood == null) {
			throw new NullPointerException("neighbourhood");
		}

		this.lattice = lattice;
		this.neighbourhood = neighbourhood;
	}

	@Override
	public void update(final Cell cell) throws Exception {
		/* The neighbourhood gets cached on 1st access. */
		neighbourhood.neighbours(cell);
	}

	public String toString() {
		return this.getClass().getSimpleName() + " <"
				+ neighbourhood.getClass().getSimpleName() + ">";
	}
}
