package rules.cell;

import java.awt.Color;
import java.util.concurrent.ExecutorService;

import neighbourhood.Neighbourhood;
import neighbourhood.VanNeumannCardinal;
import rules.AbstractRule;
import shapedetector.ShapeDetector;
import utils.graph.Graph;
import ca.Cell;
import ca.Lattice;

/**
 * Groups cells of similar state together into blobs.
 * <p>
 * NOTE: this rule also changes the lattice's cell neighbourhoods. It requires
 * that cells have Van Neumann neighbourhoods, with r=1.
 */
public class CellConnectRule<V> extends AbstractRule<Cell> {
	protected final Lattice<Color> colourLattice;
	protected final Lattice<V> doubleLattice;
	protected final Neighbourhood neighbourhood;
	protected final Graph<Cell> blobGraph;

	protected ExecutorService executorService;
	protected final CellConnectRule<V> server = this;

	public static final Color EDGE_COLOUR = Color.gray;
	public static final Color UNMERGED_COLOUR = Color.red;
	public static final Color MERGED_COLOUR = Color.white;

	public CellConnectRule(final Lattice<V> doubleLattice,
			final Graph<Cell> blobGraph,
			final VanNeumannCardinal<V> neighbourhood,
			final Lattice<Color> colourLattice) {
		super();
		if (colourLattice == null) {
			throw new NullPointerException("colourLattice");
		}
		if (doubleLattice == null) {
			throw new NullPointerException("doubleLattice");
		}
		if (blobGraph == null) {
			throw new NullPointerException("blobMap");
		}
		if (neighbourhood == null) {
			throw new NullPointerException("neighbourhood");
		}

		this.colourLattice = colourLattice;
		this.doubleLattice = doubleLattice;
		this.blobGraph = blobGraph;
		this.neighbourhood = neighbourhood;
	}

	// @Override
	// public void prepare() throws Exception {
	// super.prepare();
	// executorService = Executors.newSingleThreadExecutor();
	// }
	//
	// @Override
	// public void complete() throws Exception {
	// super.complete();
	// executorService.shutdown();
	// executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
	// }

	@Override
	public void update(final Cell cell) throws Exception {
		/*
		 * Only merges blobs from the spaces in between edges. (Saves a little
		 * time when they aren't necessary.)
		 */
		V cellState = doubleLattice.getState(cell);
		if (cellState.equals(CellStates.ACTIVE)) {
			if (ShapeDetector.debug) {
				colourLattice.setState(cell, EDGE_COLOUR);
			}
			return;
		}
		if (ShapeDetector.debug) {
			colourLattice.setState(cell, UNMERGED_COLOUR);
		}

		for (Cell neighbour : neighbourhood.neighbours(cell)) {
			if (neighbour != cell) {
				if (cellState.equals(doubleLattice.getState(neighbour))) {
					/* Edge finder can be integrated here. See method below... */
					blobGraph.connect(cell, neighbour);

					if (ShapeDetector.debug) {
						colourLattice.setState(cell, MERGED_COLOUR);
						colourLattice.setState(neighbour, MERGED_COLOUR);
					}
				}
			}
		}
	}

	// /*
	// * The edge finder step can be run from here, something like this:
	// */
	// private void edgeFinder() {
	// double difference = ColourCompare.getDifference(
	// lattice.getState(cell), lattice.getState(neighbour));
	// if (difference < epsilon) {
	// blobMap.mergeCells(cell, neighbour);
	// }
	// }
}
