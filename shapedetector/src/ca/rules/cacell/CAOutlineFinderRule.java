package ca.rules.cacell;

import java.util.ArrayList;
import java.util.List;

import ca.CA;
import ca.CACell;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;

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
public class CAOutlineFinderRule extends CACellRule {
	protected CAShapeDetector ca;

	public CAOutlineFinderRule(CAShapeDetector ca) {
		super(ca);
		this.ca = ca;
	}

	public void update(CACell cell) {
		/* Assumes that cell has a Van Neumann neighbourhood, with r=1 */
		CAProtoShape protoShape = ca.getProtoShape(cell);

		List<CACell> neighbourhood = cell.getNeighbourhood();
		for (CACell neighbour : neighbourhood) {
			if (neighbour != cell && neighbour != CA.paddingCell
					&& protoShape != ca.getProtoShape(neighbour)) {
				/*
				 * May need to make a copy of the cell, so that this CA's cells
				 * continue to use a standard neighbourhood. But this causes
				 * complications when checking whether those cells are contained
				 * in a shape.
				 */
				// CACell outlineCell = new CACell(cell.getCoordinates(),
				// CACell.INACTIVE, meetOutlineNeighbours(cell));
				/* Expands the outlineCell's neighbourhood. */
				cell.setNeighbourhood(meetOutlineNeighbours(cell));
				protoShape.addOutlineCell(cell);
			}
		}
	}

	/**
	 * Gathers the specified cell's Moore neighbourhood with r=1, not including
	 * the current cell. Places cells in clockwise order, starting with the cell
	 * directly above this one. The sequence determines how outline cells will
	 * be ordered.
	 * 
	 * @param cell
	 *            Cell to get neighbourhood of.
	 * @return The cell's neighbourhood.
	 */
	protected List<CACell> meetOutlineNeighbours(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		List<CACell> neighbourhood = new ArrayList<CACell>(8);
		// neighbourhood.add(getCell(coordinates[0], coordinates[1]));

		neighbourhood.add(ca.getCell(coordinates[0], coordinates[1] - 1));
		neighbourhood.add(ca.getCell(coordinates[0] + 1, coordinates[1] - 1));
		neighbourhood.add(ca.getCell(coordinates[0] + 1, coordinates[1]));
		neighbourhood.add(ca.getCell(coordinates[0] + 1, coordinates[1] + 1));
		neighbourhood.add(ca.getCell(coordinates[0], coordinates[1] + 1));
		neighbourhood.add(ca.getCell(coordinates[0] - 1, coordinates[1] + 1));
		neighbourhood.add(ca.getCell(coordinates[0] - 1, coordinates[1]));
		neighbourhood.add(ca.getCell(coordinates[0] - 1, coordinates[1] - 1));
		return neighbourhood;
	}
}
