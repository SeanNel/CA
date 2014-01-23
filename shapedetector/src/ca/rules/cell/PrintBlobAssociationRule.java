package ca.rules.cell;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.shapedetector.BlobMap;
import exceptions.CAException;

/**
 * For debugging. Prints out the blob associations for each cell.
 */
public class PrintBlobAssociationRule<V> extends CellRule<V> {
	protected final BlobMap<V> blobMap;

	public PrintBlobAssociationRule(final Lattice<V> lattice,
			final Neighbourhood<V> neighbourhoodModel, final BlobMap<V> blobMap)
			throws CAException {
		super(lattice, neighbourhoodModel);
		this.blobMap = blobMap;
	}

	@Override
	public void update(final Cell<V> cell) throws CAException {
		System.out.println("x=" + cell.getCoordinates()[0] + ", y="
				+ cell.getCoordinates()[1] + " > " + blobMap.getBlob(cell));
	}
}
