package ca.rules.cell;

import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;
import exceptions.CAException;

/**
 * Finds outline cells of shapes, ensuring that outlines are closed loops. Note
 * that the apparent thickness of edges is irrelevant. Each shape's outline is
 * determined by a single layer of cells and this is ensured by the algorithm.
 * <p>
 * This step may not be necessary if an outline arranger algorithm can work
 * efficiently without it.
 * <p>
 * Assumes that cells have Van Neumann neighbourhoods, with r=1.
 */
public class OutlineFinderRule<V> extends CellRule<V> {
	protected final BlobMap<V> blobMap;

	public OutlineFinderRule(final Lattice<V> lattice,
			final Neighbourhood<V> neighbourhoodModel, final BlobMap<V> blobMap)
			throws CAException {
		super(lattice, neighbourhoodModel);
		this.blobMap = blobMap;
	}

	@Override
	public void update(final Cell<V> cell) throws CAException {
		Blob<V> blob = blobMap.getBlob(cell);

		List<Cell<V>> neighbourhood = cell.getNeighbourhood();
		for (Cell<V> neighbour : neighbourhood) {
			if (neighbour != cell && blob != blobMap.getBlob(neighbour)) {
				blob.addOutlineCell(cell);
				return;
			}
		}
	}
}
