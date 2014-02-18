package rules.cell;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import neighbourhood.Neighbourhood;

import rules.AbstractRule;

import ca.Cell;
import ca.Lattice;

/**
 * Removes noise from the image.
 * <p>
 * It may be possible to improve performance by caching the colour differences.
 * 
 * @author Sean
 */
public class NoiseRemoverRule extends AbstractRule<Cell> {
	protected final Lattice<Color> lattice;
	protected final Neighbourhood neighbourhood;
	protected final double epsilon;

	public NoiseRemoverRule(final Lattice<Color> lattice,
			final Neighbourhood neighbourhood, final double epsilon) {
		super();
		if (lattice == null) {
			throw new NullPointerException("lattice");
		}
		if (neighbourhood == null) {
			throw new NullPointerException("neighbourhood");
		}

		this.lattice = lattice;
		this.neighbourhood = neighbourhood;
		this.epsilon = epsilon;
	}

	@Override
	public void update(final Cell cell) throws Exception {
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
	 * @throws Exception
	 */
	protected void meanState(final Cell cell) throws Exception {
		Collection<Cell> neighbours = neighbourhood.neighbours(cell);
		/*
		 * Making the colours List an array may or may not speed things up.
		 */
		List<Color> colours = new ArrayList<Color>(neighbours.size());
		Color cellColour = lattice.getState(cell);
		double maxDifference = 0d;

		for (Cell neighbour : neighbours) {
			if (neighbour != cell) {
				Color neighbourColour = lattice.getState(neighbour);
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
			lattice.setState(cell, newColour);
		}
	}

	/**
	 * Sets the state of the cell to the median state of its neighbourhood. That
	 * is, a colour that exists among at least one of the neighbourhood cells,
	 * that is in the middle of the range of colours from all the neighbourhood
	 * cells.
	 * 
	 * @param cell
	 * @throws Exception
	 */
	protected void medianState(final Cell cell) throws Exception {
		Collection<Cell> neighbours = neighbourhood.neighbours(cell);
		List<Color> colours = new ArrayList<Color>(neighbours.size());
		Color cellColour = lattice.getState(cell);

		for (Cell neighbour : neighbours) {
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
