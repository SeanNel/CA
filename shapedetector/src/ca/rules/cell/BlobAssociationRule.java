package ca.rules.cell;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * Creates a CABlob object for each cell.
 */
public class BlobAssociationRule extends CellRule {
	protected final BlobMap blobMap;

	public BlobAssociationRule(Lattice<Cell> lattice,
			Neighbourhood neighbourhoodModel, BlobMap blobMap)
			throws CAException {
		super(lattice, neighbourhoodModel);
		if (blobMap == null) {
			throw new NullParameterException("blobMap");
		}

		this.blobMap = blobMap;
	}

	public void update(Cell cell) {
		Blob blob = new Blob(cell);
		blobMap.setBlob(cell, blob);
		blobMap.addBlob(blob);
		// System.out.println(cell.getCoordinates()[0] + ", " +
		// cell.getCoordinates()[1]);
	}
}
