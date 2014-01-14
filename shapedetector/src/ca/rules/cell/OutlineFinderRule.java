package ca.rules.cell;

import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.MooreOutline;
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
public class OutlineFinderRule extends CellRule {
	protected final MooreOutline outlineNeighbourhood;
	protected final BlobMap blobMap;

	public OutlineFinderRule(Lattice<Cell> lattice, Neighbourhood neighbourhoodModel,
			BlobMap blobMap) throws CAException {
		super(lattice, neighbourhoodModel);
		this.blobMap = blobMap;
		outlineNeighbourhood = new MooreOutline(lattice, blobMap);
	}

	public void update(Cell cell) {
		Blob blob = blobMap.getBlob(cell);

		List<Cell> neighbourhood = cell.getNeighbourhood();
		for (Cell neighbour : neighbourhood) {
			if (neighbour != cell && blob != blobMap.getBlob(neighbour)) {

				/*
				 * May need to make a copy of the cell, so that this CA's cells
				 * continue to use a standard neighbourhood. But this causes
				 * complications when checking whether those cells are contained
				 * in a shape.
				 */
				// CACell outlineCell = new CACell(cell.getCoordinates(),
				// CACell.INACTIVE, meetOutlineNeighbours(cell));
				/* Expands the outlineCell's neighbourhood. */
				cell.setNeighbourhood(outlineNeighbourhood
						.gatherNeighbours(cell));
				blob.addOutlineCell(cell);
				return;
			}
		}
	}
}
