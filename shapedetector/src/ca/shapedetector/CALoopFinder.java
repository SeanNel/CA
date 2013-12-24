package ca.shapedetector;

import java.util.ArrayList;
import java.util.List;

import ca.CA;
import ca.CACell;

/**
 * A basic algorithm for taking a collection of cells that form a loop and
 * places them in order.
 * <p>
 * Assumes that the cells have Moore neighbourhoods with r=1.
 */
public class CALoopFinder {
	protected List<CACell> loopCells;
	protected List<CACell> loop;

	public CALoopFinder(List<CACell> loopCells) {
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
	public List<CACell> getLoop(CACell first) {
		if (loopCells == null || loopCells.size() < 4) {
			return null;
		}

		loop = new ArrayList<CACell>(loopCells.size());
		CACell next = first;

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
	protected CACell nextOutlineCell() {
		int stepsBack = 0;
		CACell next = null;
		while (next == null && stepsBack < loop.size()) {
			CACell currentCell = loop.get(loop.size() - stepsBack - 1);
			next = nextOutlineCell(currentCell);
			stepsBack++;
		}
		return next;
	}

	protected CACell nextOutlineCell(CACell currentCell) {
		List<CACell> neighbourhood = currentCell.getNeighbourhood();

		for (CACell neighbour : neighbourhood) {
			/* outlineCells >> hashSet or bst etc? */
			if (neighbour != CA.paddingCell
					&& (!loop.contains(neighbour) || neighbour == loop.get(0))
					&& loopCells.contains(neighbour)) {
				return neighbour;
			}
		}
		return null;
	}
}
