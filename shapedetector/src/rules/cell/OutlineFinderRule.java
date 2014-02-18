package rules.cell;

import java.awt.Color;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Map;

import neighbourhood.Neighbourhood;
import neighbourhood.VanNeumannCardinal;
import rules.AbstractRule;
import shapedetector.ShapeDetector;
import utils.graph.Graph;
import ca.Cell;
import ca.Lattice;

/**
 * Finds outline cells of shapes, ensuring that outlines are closed loops. Note
 * that the apparent thickness of edges is irrelevant. Each shape's outline is
 * determined by a single layer of cells and this is ensured by the algorithm.
 * <p>
 * This step may not be necessary if an outline arranger algorithm can work
 * efficiently without it.
 * <p>
 * NOTE: this rule also changes the lattice's cell neighbourhoods. It requires
 * that cells have Van Neumann neighbourhoods, with r=1.
 */
public class OutlineFinderRule<V> extends AbstractRule<Cell> {
	protected final Lattice<Color> colourLattice;
	protected final Lattice<Double> doubleLattice;
	protected final Graph<Cell> clusterGraph;
	protected final Map<Cell, Collection<Cell>> outlineMap;
	protected final Neighbourhood neighbourhood;

	protected static final Color OUTLINE_COLOUR = new Color(200, 200, 200);// Color.black;
	protected static final Color QUIESCENT_COLOUR = Color.white;

	public OutlineFinderRule(final Lattice<Double> doubleLattice,
			final Graph<Cell> clusterGraph,
			final Map<Cell, Collection<Cell>> outlineMap,
			final VanNeumannCardinal<V> neighbourhood,
			final Lattice<Color> colourLattice) {
		super();
		if (doubleLattice == null) {
			throw new NullPointerException("doubleLattice");
		}
		if (clusterGraph == null) {
			throw new NullPointerException("clusterGraph");
		}
		if (outlineMap == null) {
			throw new NullPointerException("outlineMap");
		}
		if (neighbourhood == null) {
			throw new NullPointerException("neighbourhood");
		}
		if (colourLattice == null) {
			throw new NullPointerException("colourLattice");
		}

		this.colourLattice = colourLattice;
		this.doubleLattice = doubleLattice;
		this.clusterGraph = clusterGraph;
		this.outlineMap = outlineMap;
		this.neighbourhood = neighbourhood;
	}

	@Override
	public synchronized void update(final Cell cell) throws Exception {
		/*
		 * Only makes outlines from the spaces between edges (does not include
		 * the edges themselves).
		 */
		if (doubleLattice.getState(cell) == CellStates.QUIESCENT) {
			Collection<Cell> outlineCells;
			Cell repCell = clusterGraph.getRoot(cell);
			synchronized (repCell) {
				outlineCells = outlineMap.get(repCell);
				if (outlineCells == null) {
					outlineCells = new LinkedList<Cell>();
					outlineMap.put(repCell, outlineCells);
				}
			}

			for (Cell neighbour : neighbourhood.neighbours(cell)) {
				/*
				 * It should be possible to speed this up further by adding a
				 * specialized isConnected method that only checks immediate
				 * neighbours.
				 */
				if (neighbour != cell
						&& !clusterGraph.isConnected(cell, neighbour)) {
					outlineCells.add(cell);

					doubleLattice.setState(cell, CellStates.ACTIVE);
					if (ShapeDetector.debug) {
						colourLattice.setState(cell, OUTLINE_COLOUR);
					}
					return;
				}
			}
		}
		doubleLattice.setState(cell, CellStates.QUIESCENT);
		if (ShapeDetector.debug) {
			colourLattice.setState(cell, QUIESCENT_COLOUR);
		}
	}
}
