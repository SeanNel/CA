package ca.shapedetector.blob;

import java.util.ArrayList;
import java.util.List;

import ca.Cell;

/**
 * A basic algorithm for taking a collection of cells that form a loop and
 * placing them in order.
 * <p>
 * Assumes that the cells have Moore neighbourhoods with r=1.
 */
public class LoopFinder<V> {
	protected final List<Cell<V>> unorderedCells;
	protected final List<Cell<V>> orderedCells;

	/**
	 * Constructor.
	 * 
	 * @param unorderedCells
	 */
	public LoopFinder(final List<Cell<V>> unorderedCells) {
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
	 */
	public List<Cell<V>> getLoop(final Cell<V> first) {
		if (unorderedCells == null || unorderedCells.size() < 4) {
			return null;
		}

		Cell<V> next = first;

		do {
			orderedCells.add(next);
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
	 * 
	 * @param previousCell
	 *            The previous outline cell.
	 * @param currentCell
	 *            The current outline cell.
	 * @return The next outline cell.
	 */
	protected Cell<V> nextOutlineCell() {
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
	 */
	protected Cell<V> nextOutlineCell(final Cell<V> currentCell) {
		List<Cell<V>> neighbourhood = currentCell.getNeighbourhood();

		for (Cell<V> neighbour : neighbourhood) {
			if ((!orderedCells.contains(neighbour) || neighbour == orderedCells
					.get(0)) && unorderedCells.contains(neighbour)) {
				return neighbour;
			}
		}
		return null;
	}
}
