package rules.cell;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Map;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

import path.SDPath;
import path.VertexIterator;

import rules.AbstractRule;
import shapedetector.ShapeDetector;
import utils.graph.Graph;

import ca.Cell;
import ca.Lattice;

/**
 * Gets the rate of change of gradients around each (outline) cell.
 * <p>
 * Is effectively a combination of 3 steps:
 * <ul>
 * <li>Gets the mean bearing (i.e. gradient) of a tangent line to the blob's
 * outline path at the cell's position.</li>
 * <li>Gets f'(x), where f(x) is the mean bearing of outline cell x.</li>
 * <li>Gets a value z that is proportional to f''(x), a measure of the rate of
 * change in bearings at the point.</li>
 * </ul>
 * 
 * @author Sean
 */
public class GetBearingConcentrationRule extends AbstractRule<Cell> {
	protected final Map<Cell, SDPath> pathMap;
	protected final Graph<Cell> clusterGraph;

	protected final Lattice<Double> doubleLattice;
	protected final Lattice<Color> colourLattice;

	protected final int r;

	protected final static Color BG_COLOUR = Color.white;

	public GetBearingConcentrationRule(final Lattice<Double> doubleLattice,
			final Graph<Cell> clusterGraph, final Map<Cell, SDPath> pathMap,
			final int r, final Lattice<Color> colourLattice) {
		super();
		if (doubleLattice == null) {
			throw new NullPointerException("doubleLattice");
		}
		if (clusterGraph == null) {
			throw new NullPointerException("clusterGraph");
		}
		if (pathMap == null) {
			throw new NullPointerException("pathMap");
		}
		if (colourLattice == null) {
			throw new NullPointerException("colourLattice");
		}

		this.pathMap = pathMap;
		this.clusterGraph = clusterGraph;
		this.doubleLattice = doubleLattice;
		this.colourLattice = colourLattice;
		this.r = r;
	}

	@Override
	public void update(final Cell cell) throws Exception {
		Cell repCell = clusterGraph.getRoot(cell);
		SDPath path = pathMap.get(repCell);
		/*
		 * Only gets the gradient of the cell if it is part of an outline.
		 */
		if (doubleLattice.getState(cell) == CellStates.QUIESCENT) {
			ignore(cell);
			return;
		}
		int[] x = cell.getCoordinates();
		double state = state(path, x[0], x[1]);
		Color colour = getColour(state); // FastMath.PI * 2
		// colour = Color.red;
		doubleLattice.setState(cell, state);
		/*
		 * Displays data on the image as greyscale values.
		 */
		if (ShapeDetector.debug) {
			colourLattice.setState(cell, colour);
		}
	}

	private void ignore(Cell cell) throws Exception {
		doubleLattice.setState(cell, Double.NaN);
		if (ShapeDetector.debug) {
			// colourLattice.setState(cell, BG_COLOUR);
		}
	}

	protected Color getColour(double value) {
		float c = (float) value;
		return new Color(c, c, c);
	}

	/** Gets the state to set the cell to. */
	public double state(SDPath path, int x, int y) {
		/* TODO: Something is not quite right with the result... */
		double[] ddf = ddf(path, x, y);
		// double value = angle(ddf);
		double state = length(ddf);
		return state / 1.5d;
	}

	/**
	 * Gets the tangent of the path at the point (x,y).
	 * 
	 * @param path
	 * @param x
	 * @param y
	 */
	public double[] ddf(SDPath path, int x, int y) {
		VertexIterator iterator = new VertexIterator(path.getVertices(),
				VertexIterator.Direction.FORWARD, false);
		if (!iterator.hasNext()) {
			/* TODO: Is this check still necessary? */
			return new double[] { 0, 0 };
		}

		/* Finds where the point lies on the path. */
		Point2D vertex = null;
		while (iterator.hasNext()) {
			vertex = iterator.next();
			if ((int) vertex.getX() == x && (int) vertex.getY() == y) {
				break;
			}
		}

		/* Allows iteration across the start and end points of the path. */
		iterator.setContinuity(true);
		VertexIterator reverseIterator = iterator.clone();
		reverseIterator.setDirection(VertexIterator.Direction.REVERSE);

		/* Gets vectors representing the bearings between adjacent cells. */
		double[][] vectors1 = vectors(reverseIterator, vertex, r);
		double[][] vectors2 = vectors(iterator, vertex, r);
		reverseVectors(vectors2);

		/*
		 * Diagonal cells have length root 2, all others should have length 1.
		 * Now we rescale them all to the same size so that they have equal
		 * weight.
		 */
		unitVectors(vectors1);
		unitVectors(vectors2);

		/* Gets the mean bearings before and after the point. */
		double[] bearing1 = meanBearing(vectors1);
		double[] bearing2 = meanBearing(vectors2);

		/* Finds the difference between the 2 bearings. */
		/* TODO: Something does not seem quite right with the result... */
		return new double[] { bearing2[0] - bearing1[0],
				bearing2[1] - bearing1[1] };
	}

	/**
	 * Gets an array of vectors of adjacent vertices.
	 * 
	 * @param iterator
	 *            a <code>VertexIterator</code> that may iterate forwards, or
	 *            backwards. It is assumed that it continues iterating across
	 *            the start and end points.
	 * @param vertex
	 *            the starting point
	 * @param r
	 *            the number of vertices to include around the starting point
	 * @return
	 */
	private double[][] vectors(final VertexIterator iterator,
			final Point2D vertex, final int r) {
		double[][] vectors = new double[r][2];
		Point2D a = vertex;
		for (int i = 0; i < r; i++) {
			Point2D b = iterator.next();

			/* Gets the vector AB */
			vectors[i][0] = b.getX() - a.getX();
			vectors[i][1] = b.getY() - a.getY();

			a = b;
		}
		return vectors;
	}

	/**
	 * Gets the mean bearing of an array of vectors as a vector.
	 * 
	 * @param vectors
	 * @return
	 */
	private static double[] meanBearing(final double[][] vectors) {
		double x = 0d;
		double y = 0d;
		int n = vectors.length;
		for (int i = 0; i < n; i++) {
			x += vectors[i][0];
			y += vectors[i][1];
		}
		return new double[] { x / (double) n, y / (double) n };
	}

	/**
	 * Reverses the direction of the vectors. Changes the vectors in place.
	 * 
	 * @param vectors
	 * @return
	 */
	private double[][] reverseVectors(double[][] vectors) {
		int n = vectors.length;
		for (int i = 0; i < n; i++) {
			vectors[i][0] *= -1d;
			vectors[i][1] *= -1d;
		}
		return vectors;
	}

	/**
	 * Gets the unit vectors of an array of vectors, i.e. scales the vectors to
	 * fit on the unit circle. Changes the vectors in place.
	 * 
	 * @param vectors
	 * @return
	 */
	private static double[][] unitVectors(double[][] vectors) {
		int n = vectors.length;
		for (int i = 0; i < n; i++) {
			double x0 = vectors[i][0];
			double y0 = vectors[i][1];

			double modulus = FastMath.sqrt(x0 * x0 + y0 * y0);
			vectors[i][0] = x0 / modulus;
			vectors[i][1] = y0 / modulus;
		}
		return vectors;
	}

	/**
	 * Gets the length of a vector.
	 * 
	 * @param vector
	 * @return
	 */
	private static double length(final double[] vector) {
		double x = vector[0];
		double y = vector[1];
		return FastMath.sqrt(x * x + y * y);
	}

	/**
	 * Concatenates two vector arrays, creating one longer array.
	 * 
	 * @param vectors1
	 * @param vectors2
	 * @return
	 */
	private double[][] concat(double[][] vectors1, double[][] vectors2) {
		double[][] vectors = new double[vectors1.length + vectors2.length][2];
		for (int i = 0; i < vectors1.length; i++) {
			vectors[i] = vectors1[i];
		}
		for (int i = 0; i < vectors2.length; i++) {
			vectors[i + vectors1.length] = vectors1[i];
		}
		return vectors;
	}

	/**
	 * Returns a measure of the vector concentration, i.e. how closely they are
	 * distributed. For an array of unit vectors, the result is in the interval
	 * [0,1].
	 * 
	 * @param vectors
	 * @return
	 */
	private static double concentration(final double[][] vectors) {
		double[] mean = meanBearing(vectors);
		return length(mean);
	}

	/**
	 * Returns the bearing of a vector as an angle in [0,2Pi).
	 * 
	 * @param r
	 * @return
	 */
	private static double bearing(final double[] r) {
		double meanBearing = FastMath.atan2(r[0], r[1]);
		return MathUtils.reduce(meanBearing, 2d * FastMath.PI, 0d);
	}
}
