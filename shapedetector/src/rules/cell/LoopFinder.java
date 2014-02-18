package rules.cell;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import neighbourhood.MooreOutline;

import ca.Cell;
import ca.Lattice2D;

/**
 * A basic algorithm for taking a collection of cells that form a loop and
 * placing them in order.
 * <p>
 * Assumes that the cells have Moore neighbourhoods with r=1.
 */
public class LoopFinder<V> {
	protected final MooreOutline<V> outlineNeighbourhood;

	/**
	 * Constructor.
	 * 
	 * @param lattice
	 * @throws NullPointerException
	 *             if lattice is null
	 */
	public LoopFinder(final Lattice2D<V> lattice) {
		if (lattice == null) {
			throw new NullPointerException("lattice");
		}
		outlineNeighbourhood = new MooreOutline<V>(lattice);
	}

	/**
	 * Places outline cells in linear sequence to form a loop, starting with the
	 * specified cell.
	 * 
	 * @bug Sometimes does not find a loop of very jagged-edged shapes.
	 * 
	 * @param first
	 *            The first cell in the loop.
	 * @throws Exception
	 */
	public List<Cell> getLoop(final Collection<Cell> unorderedCells,
			final Cell first) throws Exception {
		if (unorderedCells == null || unorderedCells.size() < 4) {
			return null;
		}
		/*
		 * TODO: May get better performance with loop as a HashMap or BST,
		 * because we check whether a cell is contained in it very often.
		 */
		ArrayList<Cell> orderedCells = new ArrayList<Cell>(
				unorderedCells.size());

		Cell next = first;

		// System.out.println("***");
		do {
			orderedCells.add(next);
			// System.out.println(next.getCoordinates()[0] + ", " +
			// next.getCoordinates()[1]);
			/* For debugging */
			// graphics.ShapeFrame.setTheme(SDPanel.HIGHLIGHT_THEME);
			// Blob.display(orderedCells);
			next = nextOutlineCell(unorderedCells, orderedCells);
		} while (next != first);

		return orderedCells;
	}

	/**
	 * Finds the next outline cell. Assumes the cell's neighbourhood is a 3*3
	 * square. When there is ambiguity, selects a cell in clockwise order,
	 * starting from above. Assumes that the current cell is not included in its
	 * own neighbourhood.
	 * <p>
	 * If the path runs into a dead-end (i.e. a single column of edge cells
	 * projecting out sideways from the path), it doubles back on itself to a
	 * point where it can continue again.
	 * 
	 * @param previousCell
	 *            The previous outline cell.
	 * @param currentCell
	 *            The current outline cell.
	 * @return The next outline cell.
	 * @throws Exception
	 */
	protected Cell nextOutlineCell(final Collection<Cell> unorderedCells,
			final ArrayList<Cell> orderedCells) throws Exception {
		int stepsBack = 0;
		Cell next = null;
		while (next == null && stepsBack < orderedCells.size()) {
			int n = orderedCells.size() - stepsBack - 1;
			Cell currentCell = orderedCells.get(n);
			next = nextOutlineCell(unorderedCells, orderedCells, currentCell);
			stepsBack++;
		}
		return next;
	}

	/**
	 * Gets the cell next to the specified cell.
	 * 
	 * @param currentCell
	 * @return
	 * @throws Exception
	 */
	protected Cell nextOutlineCell(final Collection<Cell> unorderedCells,
			final ArrayList<Cell> orderedCells, final Cell currentCell)
			throws Exception {
		Collection<Cell> neighbours = outlineNeighbourhood
				.neighbours(currentCell);
		Cell firstCell = orderedCells.get(0);

		for (Cell neighbour : neighbours) {
			/*
			 * The neighbours are guaranteed to be from the same blob, but are
			 * not necessarily all outline cells.
			 */
			// blobMap.getBlob(cell) == blobMap.getBlob(neighbour)
			if (unorderedCells.contains(neighbour)
					&& (!orderedCells.contains(neighbour) || neighbour == firstCell)) {
				return neighbour;
			}
		}
		return null;
	}
}
