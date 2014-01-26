package ca.rules.cell;

import exceptions.CAException;

import java.awt.Color;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.VanNeumannCardinal;
import ca.shapedetector.BlobMap;

/**
 * Groups cells of similar colour together into blobs.
 */
public class BlobMergeRule extends CellRule<Color> {
	protected final BlobMap<Color> blobMap;
	protected final double epsilon;

	public BlobMergeRule(final Lattice<Color> lattice,
			final BlobMap<Color> blobMap, final double epsilon)
			throws CAException {
		super(lattice, new VanNeumannCardinal<Color>(lattice));
		this.blobMap = blobMap;
		this.epsilon = epsilon;
	}

	@Override
	public void update(final Cell<Color> cell) throws CAException {
		/*
		 * Do not generate shapes from the edges, only from the spaces in
		 * between.
		 */
		if (lattice.getState(cell).equals(EdgeFinderRule.EDGE_COLOUR)) {
			return;
		}

		List<Cell<Color>> neighbourhood = neighbourhoodModel
				.gatherNeighbours(cell);
		cell.setNeighbourhood(neighbourhood);

		for (Cell<Color> neighbour : neighbourhood) {
			/*
			 * Checks whether blobs have merged already to save time if they
			 * were.
			 */
			if (neighbour != cell
					&& blobMap.getBlob(neighbour) != blobMap.getBlob(cell)) {
				/*
				 * The edge finder step can be run from here.
				 */
				// double difference = ColourCompare.getDifference(
				// lattice.getState(cell), lattice.getState(neighbour));
				// if (difference < epsilon) {
				// blobMap.mergeCells(cell, neighbour);
				// }

				/*
				 * In conjunction with the edge finder, this works a little
				 * faster.
				 */
				if (neighbour.getState()
						.equals(EdgeFinderRule.QUIESCENT_COLOUR)) {
					blobMap.mergeCells(cell, neighbour);
				}
			}
		}
	}
}
