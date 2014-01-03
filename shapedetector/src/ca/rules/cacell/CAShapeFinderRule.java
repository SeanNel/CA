package ca.rules.cacell;

import graphics.ColourCompare;

import java.util.ArrayList;
import java.util.List;

import ca.CA;
import ca.CACell;
import ca.shapedetector.CAShapeDetector;

/**
 * Groups cells of similar colour together into shapes.
 */
public class CAShapeFinderRule extends CACellRule {
	protected CAShapeDetector ca;

	public CAShapeFinderRule(CAShapeDetector ca) {
		super(ca);
		this.ca = ca;
	}

	public void update(CACell cell) {
		/*
		 * Do not generate shapes from the edges, only from the spaces in
		 * between.
		 */
		if (ca.getColour(cell).equals(CAEdgeFinderRule.EDGE_COLOUR)) {
			return;
		}

		cell.setNeighbourhood(gatherCardinalNeighbours(cell));

		List<CACell> neighbourhood = cell.getNeighbourhood();
		for (CACell neighbour : neighbourhood) {
			if (neighbour != cell && neighbour != CA.paddingCell
					&& ca.getProtoShape(neighbour) != ca.getProtoShape(cell)) {
				float difference = ColourCompare.getDifference(
						ca.getColour(cell), ca.getColour(neighbour));
				if (difference < ca.getEpsilon()) {
					ca.mergeCells(cell, neighbour);
				}
			}
		}
	}

	/**
	 * Caches the neighbouring cells of the specified cell. Optimized for
	 * VanNeumann neighbourhood, r=1. Does not include the cell in its own
	 * neighbourhood.
	 * 
	 * @param cell
	 *            The cell to initialize.
	 */
	protected List<CACell> gatherCardinalNeighbours(CACell cell) {
		int[] coordinates = cell.getCoordinates();
		List<CACell> neighbourhood = new ArrayList<CACell>();
		// neighbourhood.add(getCell(coordinates[0], coordinates[1]));
		neighbourhood.add(ca.getCell(coordinates[0], coordinates[1] - 1));
		neighbourhood.add(ca.getCell(coordinates[0], coordinates[1] + 1));
		neighbourhood.add(ca.getCell(coordinates[0] - 1, coordinates[1]));
		neighbourhood.add(ca.getCell(coordinates[0] + 1, coordinates[1]));
		return neighbourhood;
	}
}
