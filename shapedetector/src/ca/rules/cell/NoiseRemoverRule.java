package ca.rules.cell;

import exceptions.CAException;
import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;

/**
 * Removes noise from the image.
 * <p>
 * It may be possible to improve performance by caching the colour differences.
 * 
 * @author Sean
 */
public class NoiseRemoverRule extends CellRule<Color> {
	protected final double epsilon;

	public NoiseRemoverRule(final Lattice<Color> lattice,
			final Neighbourhood<Color> neighbourhoodModel, final double epsilon)
			throws CAException {
		super(lattice, neighbourhoodModel);
		this.epsilon = epsilon;
	}
	
	// @Override
	// public void complete() {
	// super.complete();
	// }

	@Override
	public void update(final Cell<Color> cell) throws CAException {
		/*
		 * For some reason this rule performs faster when this is set from here
		 * and not in a separate rule.
		 */
		cell.setNeighbourhood(neighbourhoodModel.gatherNeighbours(cell));

		meanState(cell);

		/*
		 * Taking the mean value is similar to Gaussian blur, but taking the
		 * median should be more effective at removing different kinds of noise.
		 * At this time though, meanColour seems to work better.
		 */
		// medianState(cell);
	}

	/**
	 * Sets the state of the cell to the mean state of its neighbourhood. That
	 * is, the average colour. This colour may not appear among any of the
	 * original cells.
	 * 
	 * @param cell
	 */
	protected void meanState(final Cell<Color> cell) {
		List<Cell<Color>> neighbourhood = cell.getNeighbourhood();
		List<Color> colours = new ArrayList<Color>(neighbourhood.size());
		Color cellColour = cell.getState();
		double maxDifference = 0d;

		for (Cell<Color> neighbour : neighbourhood) {
			if (neighbour != cell) {
				Color neighbourColour = neighbour.getState();
				colours.add(neighbourColour);
				double difference = ColourCompare.getDifference(cellColour,
						neighbourColour);
				if (difference > epsilon) {
					return;
				} else if (difference > maxDifference) {
					maxDifference = difference;
				}
			}
		}

		if (maxDifference < epsilon) {
			Color newColour = ColourCompare.meanColour(colours);
			cell.setState(newColour);
		}
	}

	/**
	 * Sets the state of the cell to the median state of its neighbourhood. That
	 * is, a colour that exists among at least one of the neighbourhood cells,
	 * that is in the middle of the range of colours from all the neighbourhood
	 * cells.
	 * 
	 * @param cell
	 */
	protected void medianState(final Cell<Color> cell) {
		List<Cell<Color>> neighbourhood = cell.getNeighbourhood();
		List<Color> colours = new ArrayList<Color>(neighbourhood.size());
		Color cellColour = lattice.getState(cell);

		for (Cell<Color> neighbour : neighbourhood) {
			if (neighbour != cell) {
				Color neighbourColour = lattice.getState(neighbour);
				colours.add(neighbourColour);
				double difference = ColourCompare.getDifference(cellColour,
						neighbourColour);
				if (difference > epsilon) {
					return;
				}
			}
		}

		Color newColour = ColourCompare.medianColour(cellColour, colours);
		lattice.setState(cell, newColour);
	}
}
