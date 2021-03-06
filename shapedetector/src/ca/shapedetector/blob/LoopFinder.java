package ca.shapedetector.blob;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.MooreOutline;
import ca.shapedetector.BlobMap;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * A basic algorithm for taking a collection of cells that form a loop and
 * placing them in order.
 * <p>
 * Assumes that the cells have Moore neighbourhoods with r=1.
 */
public class LoopFinder<V> {
	protected final MooreOutline<V> outlineNeighbourhood;

	protected final List<Cell<V>> unorderedCells;
	protected final List<Cell<V>> orderedCells;

	/**
	 * Constructor.
	 * 
	 * @param unorderedCells
	 * @throws NullParameterException
	 */
	public LoopFinder(final List<Cell<V>> unorderedCells,
			final Lattice<V> lattice, final BlobMap<V> blobMap)
			throws NullParameterException {
		outlineNeighbourhood = new MooreOutline<V>(lattice, blobMap);
		this.unorderedCells = unorderedCells;
		/*
		 * TODO: May get better performance with loop as a HashMap or BST,
		 * because we check whether a cell is contained in it very often.
		 */
		orderedCells = new ArrayList<Cell<V>>(unorderedCells.size());
	}

	/**
	 * Places outline cells in linear sequence to form a loop, starting with the
	 * specified cell.
	 * 
	 * @bug Sometimes does not find a loop of very jagged-edged shapes.
	 * 
	 * @param first
	 *            The first cell in the loop.
	 * @throws CAException
	 */
	public List<Cell<V>> getLoop(final Cell<V> first) throws CAException {
		if (unorderedCells == null || unorderedCells.size() < 4) {
			return null;
		}

		Cell<V> next = first;

//		System.out.println("***");
		do {
			orderedCells.add(next);
//			System.out.println(next.getCoordinates()[0] + ", " + next.getCoordinates()[1]);
			/* For debugging */
			// graphics.ShapeFrame.setTheme(SDPanel.HIGHLIGHT_THEME);
			// Blob.display(orderedCells);
			next = nextOutlineCell();
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
	 * @throws CAException
	 */
	protected Cell<V> nextOutlineCell() throws CAException {
		int stepsBack = 0;
		Cell<V> next = null;
		while (next == null && stepsBack < orderedCells.size()) {
			int n = orderedCells.size() - stepsBack - 1;
			Cell<V> currentCell = orderedCells.get(n);
			next = nextOutlineCell(currentCell);
			stepsBack++;
		}
		return next;
	}

	/**
	 * Gets the cell next to the specified cell.
	 * 
	 * @param currentCell
	 * @return
	 * @throws CAException
	 */
	protected Cell<V> nextOutlineCell(final Cell<V> currentCell)
			throws CAException {
		List<Cell<V>> neighbourhood = outlineNeighbourhood
				.gatherNeighbours(currentCell);

		for (Cell<V> neighbour : neighbourhood) {
			/*
			 * The neighbours are guaranteed to be from the same shape, but are
			 * not necessarily all outline cells.
			 */
			if (unorderedCells.contains(neighbour)
					&& (!orderedCells.contains(neighbour) || neighbour == orderedCells
							.get(0))) {
				return neighbour;
			}
		}
		return null;
	}
}
