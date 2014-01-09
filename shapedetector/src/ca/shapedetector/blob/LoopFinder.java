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
public class LoopFinder {
	protected List<Cell> loopCells;
	protected List<Cell> loop;

	public LoopFinder(List<Cell> loopCells) {
		this.loopCells = loopCells;
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
	public List<Cell> getLoop(Cell first) {
		if (loopCells == null || loopCells.size() < 4) {
			return null;
		}

		/*
		 * TODO: May get better performance with loop as a HashMap or BST,
		 * because we check whether a cell is contained in it very often.
		 */
		loop = new ArrayList<Cell>(loopCells.size());
		Cell next = first;

		do {
			loop.add(next);
			next = nextOutlineCell();
		} while (next != first);

		return loop;
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
	protected Cell nextOutlineCell() {
		int stepsBack = 0;
		Cell next = null;
		while (next == null && stepsBack < loop.size()) {
			Cell currentCell = loop.get(loop.size() - stepsBack - 1);
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
	protected Cell nextOutlineCell(Cell currentCell) {
		List<Cell> neighbourhood = currentCell.getNeighbourhood();

		for (Cell neighbour : neighbourhood) {
			if ((!loop.contains(neighbour) || neighbour == loop.get(0))
					&& loopCells.contains(neighbour)) {
				return neighbour;
			}
		}
		return null;
	}
}
