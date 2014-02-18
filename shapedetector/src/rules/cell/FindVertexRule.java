package rules.cell;

import java.awt.Color;
import java.util.Map;

import path.SDPath;

import neighbourhood.Neighbourhood;

import rules.AbstractRule;
import shapedetector.ShapeDetector;
import utils.graph.Graph;

import ca.Cell;
import ca.Lattice;

/**
 * Assigns probabilities to cells of them being located at shape vertices.
 * <p>
 * TODO: untested code
 * 
 * @author Sean
 */
public class FindVertexRule extends AbstractRule<Cell> {
	protected final Lattice<Double> doubleLattice;
	protected final Lattice<Color> colourLattice;
	protected final Graph<Cell> clusterGraph;
	protected final Neighbourhood neighbourhood;

	protected final int r;

	protected final static Color VERTEX_COLOUR = Color.red;
	protected final static Color BG_COLOUR = Color.white;

	public FindVertexRule(final Lattice<Double> doubleLattice,
			final Graph<Cell> clusterGraph, final Map<Cell, SDPath> pathMap,
			final Neighbourhood neighbourhood, final int r,
			final Lattice<Color> colourLattice) {
		super();
		if (doubleLattice == null) {
			throw new NullPointerException("doubleLattice");
		}
		if (clusterGraph == null) {
			throw new NullPointerException("clusterGraph");
		}
		if (neighbourhood == null) {
			throw new NullPointerException("neighbourhood");
		}
		if (colourLattice == null) {
			throw new NullPointerException("colourLattice");
		}

		this.doubleLattice = doubleLattice;
		this.clusterGraph = clusterGraph;
		this.neighbourhood = neighbourhood;
		this.r = r;
		this.colourLattice = colourLattice;
	}

	@Override
	public void update(final Cell cell) throws Exception {
		double cellState = doubleLattice.getState(cell);

		if (cellState == CellStates.QUIESCENT) { // i.e. 0d
			return;
		}

		double max = Double.MIN_VALUE;
		for (Cell neighbour : neighbourhood.neighbours(cell)) {
			// if (cell != neighbour && blob == blobMap.getBlob(neighbour)) {
			if (cell != neighbour) {
				Double neighbourState = doubleLattice.getState(neighbour);
				if (neighbourState > cellState) {
					doubleLattice.setState(cell, CellStates.QUIESCENT);
					/* For debugging */
					// colourLattice.setState(cell, BG_COLOUR);
					return;
				}
				if (neighbourState > max) {
					max = neighbourState;
				}
			}
		}

		if (cellState > max) {
			doubleLattice.setState(cell, CellStates.ACTIVE);
			if (ShapeDetector.debug) {
				colourLattice.setState(cell, VERTEX_COLOUR);
			}
		} else {
			doubleLattice.setState(cell, CellStates.QUIESCENT);
			// if (ShapeDetector.debug) {
			// colourLattice.setState(cell, BG_COLOUR);
			// }
		}
	}
}
