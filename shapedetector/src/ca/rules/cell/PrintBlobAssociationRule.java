package ca.rules.cell;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.shapedetector.BlobMap;
import exceptions.CAException;

/**
 * For debugging. Prints out the blob associations for each cell. 
 */
public class PrintBlobAssociationRule extends CellRule {
	protected final BlobMap blobMap;

	public PrintBlobAssociationRule(Lattice<Cell> lattice,
			Neighbourhood neighbourhoodModel, BlobMap blobMap)
			throws CAException {
		super(lattice, neighbourhoodModel);
		this.blobMap = blobMap;
	}

	public void update(Cell cell) {
		System.out.println("x=" + cell.getCoordinates()[0] + ", y="
				+ cell.getCoordinates()[1] + " > " + blobMap.getBlob(cell));
	}
}
