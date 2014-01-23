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
public class BlobAssociationRule<V> extends CellRule<V> {
	protected final BlobMap<V> blobMap;

	public BlobAssociationRule(final Lattice<V> lattice,
			final Neighbourhood<V> neighbourhoodModel, final BlobMap<V> blobMap)
			throws CAException {
		super(lattice, neighbourhoodModel);
		if (blobMap == null) {
			throw new NullParameterException("blobMap");
		}

		this.blobMap = blobMap;
	}

	@Override
	public void update(final Cell<V> cell) throws CAException {
		Blob<V> blob = new Blob<V>(cell);
		blobMap.setBlob(cell, blob);
		blobMap.addBlob(blob);
		// System.out.println(cell.getCoordinates()[0] + ", " +
		// cell.getCoordinates()[1]);
	}
}
