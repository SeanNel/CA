package rules.cell;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import path.SDPath;

import rules.AbstractRule;
import shapedetector.ShapeDetector;
import shapes.AbstractShape;
import shapes.UnknownShape;

import utils.graph.Graph;

import ca.Cell;
import ca.Lattice;
import ca.Lattice2D;
import graphics.SDPanelTheme;

/**
 * Performs topological sort on cell outline clusters and produces a map of
 * corresponding SDPaths.
 * <p>
 * The sort places cells in clockwise sequence, with pseudo-random starting
 * position along the path's top boundary (depending on which cell was added to
 * the list first). Notice that this forms a closed loop, so that enveloped
 * paths are ignored (and treated separately).
 * <p>
 * Expected performance is O(8N) per cell cluster, where N is the number of
 * outline cells.
 */
public class PathFinderRule extends AbstractRule<Cell> {
	protected final Lattice<Color> colourLattice;
	protected final Lattice2D<Double> doubleLattice;
	protected final Graph<Cell> clusterGraph;
	protected final Map<Cell, Collection<Cell>> outlineMap;
	protected final Map<Cell, SDPath> pathMap;

	protected final LoopFinder<Double> loopFinder;

	protected final static int MIN_PERIMETER = 16;
	protected final static Color BG_COLOUR = Color.white;
	protected final static Color DEAD_COLOUR = new Color(200, 200, 200);

	public PathFinderRule(final Lattice2D<Double> doubleLattice,
			final Graph<Cell> clusterGraph,
			final Map<Cell, Collection<Cell>> outlineMap,
			final Map<Cell, SDPath> pathMap, final Lattice<Color> colourLattice) {
		super();
		if (doubleLattice == null) {
			throw new NullPointerException("doubleLattice");
		}
		if (clusterGraph == null) {
			throw new NullPointerException("clusterGraph");
		}
		if (outlineMap == null) {
			throw new NullPointerException("outlineMap");
		}
		if (pathMap == null) {
			throw new NullPointerException("pathMap");
		}
		if (colourLattice == null) {
			throw new NullPointerException("colourLattice");
		}

		this.outlineMap = outlineMap;
		this.clusterGraph = clusterGraph;
		this.pathMap = pathMap;
		this.doubleLattice = doubleLattice;
		this.colourLattice = colourLattice;

		loopFinder = new LoopFinder<Double>(doubleLattice);
	}

	@Override
	public void update(final Cell cell) throws Exception {
		Cell repCell = clusterGraph.getRoot(cell);
		Collection<Cell> outlineCells = outlineMap.get(repCell);

		if (doubleLattice.getState(cell) == CellStates.QUIESCENT) {
			if (ShapeDetector.debug) {
				colourLattice.setState(cell, BG_COLOUR);
			}
			return;
		} else {
			synchronized (repCell) {
				SDPath path = pathMap.get(repCell);
				if (path == null) {
					Cell first = firstOutlineCell(outlineCells);

					// System.out.println("Unarranged outline cells:");
					// System.out.println(cells);

					if (first != null) {
						// if (ShapeDetector.debug) {
						// display(cellCluster);
						// }

						/*
						 * Arranges the outline cells in topological order
						 * before creating an SDPath.
						 */
						List<Point2D> loop = new LinkedList<Point2D>();
						List<Cell> cellLoop = loopFinder.getLoop(outlineCells,
								first);
						if (cellLoop != null) {
							for (Cell c : cellLoop) {
								int[] x = c.getCoordinates();
								loop.add(new Point2D.Double(x[0], x[1]));
							}
						}

						path = new SDPath(loop);
						pathMap.put(repCell, path);

						// if (ShapeDetector.debug) {
						// display(cellLoop);
						// }

						/*
						 * This indicates the starting point of the outline
						 * path.
						 */
						if (ShapeDetector.debug) {
							// colourLattice.setState(cell, Color.red);
						}
					}

					// System.out.println("Arranged outline cells:");
					// System.out.println(cells);
					// } else if (ShapeDetector.debug) {
					// colourLattice.setState(cell, Color.white);
				}
			}
		}
	}

	private Rectangle getBounds(final Collection<Cell> cells) {
		if (cells.size() == 0) {
			throw new RuntimeException("collection of cells is empty");
		}

		Iterator<Cell> iterator = cells.iterator();
		int[] coordinates = iterator.next().getCoordinates();
		Rectangle bounds = new Rectangle(coordinates[0], coordinates[1], 1, 1);

		while (iterator.hasNext()) {
			coordinates = iterator.next().getCoordinates();
			bounds.add(coordinates[0], coordinates[1]);
		}
		return bounds;
	}

	/**
	 * Finds a cell along the top boundary.
	 * <p>
	 * It is not difficult to ensure that the top-left cell is selected, but any
	 * one at the top boundary will work, so we'll just pick one, because it's
	 * faster that way.
	 * 
	 * @param cells
	 * @param bounds
	 * @return The '1st' cell to start the loop of outline cells.
	 */
	protected Cell firstOutlineCell(final Collection<Cell> cells) {
		Rectangle bounds = getBounds(cells);
		for (Cell cell : cells) {
			/* Looks at row along top boundary: */
			if (cell.getCoordinates()[1] == bounds.getMinY()) {
				return cell;
			}
		}
		return null;
	}

	/**
	 * For debugging. Displays a blob made of the specified cells.
	 * 
	 * @param cells
	 */
	public void display(final List<Cell> cells) {
		graphics.ShapeFrame.setTheme(SDPanelTheme.DEFAULT);
		SDPath path = SDPath.getPath(cells);
		AbstractShape shape = new UnknownShape(path);
		graphics.ShapeFrame.reset(shape);
		graphics.ShapeFrame.display(shape);
	}
}
