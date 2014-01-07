package ca.rules.cell;

import exceptions.CAException;
import graphics.ColourCompare;

import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.VanNeumannCardinal;
import ca.shapedetector.BlobMap;

/**
 * Groups cells of similar colour together into shapes.
 */
public class ShapeFinderRule extends CellRule {
	protected BlobMap blobMap;
	protected float epsilon;

	public ShapeFinderRule(Lattice lattice, BlobMap blobMap, float epsilon)
			throws CAException {
		super(lattice, new VanNeumannCardinal(lattice));
		this.blobMap = blobMap;
		this.epsilon = epsilon;
	}

	public void update(Cell cell) {
		/*
		 * Do not generate shapes from the edges, only from the spaces in
		 * between.
		 */
		if (lattice.getColour(cell).equals(EdgeFinderRule.EDGE_COLOUR)) {
			return;
		}

		List<Cell> neighbourhood = neighbourhoodModel.gatherNeighbours(cell);
		/*
		 * No need to save this specialized neighbourhood, it won't be used
		 * again.
		 */
		// cell.setNeighbourhood(neighbourhood);

		for (Cell neighbour : neighbourhood) {
			if (neighbour != cell
					&& blobMap.getBlob(neighbour) != blobMap.getBlob(cell)) {
				/*
				 * This comparison method enables the rule to act without an
				 * edge finder step.
				 */
				float difference = ColourCompare.getDifference(
						lattice.getColour(cell), lattice.getColour(neighbour));
				if (difference < epsilon) {
					blobMap.mergeCells(cell, neighbour);
				}

				/*
				 * In conjunction with the edge finder, this works a little
				 * faster.
				 */
				// if (ca.getColour(cell)
				// .equals(CAEdgeFinderRule.QUIESCENT_COLOUR)) {
				// ca.mergeCells(cell, neighbour);
				// }
			}
		}
	}
}
