package math.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import math.functions.Differential;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

/**
 * Creates an interface for finding and manipulating the local and absolute
 * maxima and minima of a function.
 * 
 * @author Sean
 */
public class CriticalPoints {
	protected final UnivariateDifferentiableFunction f;
	protected final List<Double> criticalPoints;
	protected final List<Double> minima;
	protected final List<Double> maxima;
	protected final double maximum;
	protected final double minimum;
	protected final double x0;
	protected final double x1;

	/**
	 * Constructor. Solves for the critical points between x0 and x1.
	 * 
	 * @param f
	 * @param x0
	 * @param x1
	 */
	public CriticalPoints(final UnivariateDifferentiableFunction f,
			final double x0, final double x1) {
		if (f == null || x1 <= x0) {
			throw new RuntimeException();
		}

		this.f = f;
		this.x0 = x0;
		this.x1 = x1;

		/* Finds all critical points */
		Differential df = new Differential(f, 1);
		Solver solver = new DiscreteSolver(df, x0, x1);
		criticalPoints = solver.getSolutions();
		// graphics.LineChartFrame.displayData(x0, x1, f, df);

		/* Finds maxima and minima */
		int n = criticalPoints.size();
		maxima = new ArrayList<Double>(n / 2);
		minima = new ArrayList<Double>(n / 2);
		double maximum = Double.MIN_VALUE;
		double minimum = Double.MAX_VALUE;
		double maxPos = 0.0;
		double minPos = 0.0;
		for (int i = 0; i < criticalPoints.size(); i++) {
			double x = criticalPoints.get(i);

			/* Gets the 2nd derivative of f */
			DerivativeStructure xStructure = new DerivativeStructure(1, 1, 0, x);
			double df2 = df.value(xStructure).getPartialDerivative(1);

			double y = f.value(x);
			if (df2 >= 0.0) {
				maxima.add(x);
				if (y > maximum) {
					maximum = y;
					maxPos = x;
				}
			} else {
				minima.add(x);
				if (y < minimum) {
					minimum = y;
					minPos = x;
				}
			}
		}
		this.maximum = maxPos;
		this.minimum = minPos;
	}

	public double value(final double x) {
		return f.value(x);
	}

	public DerivativeStructure value(final DerivativeStructure t) {
		return f.value(t);
	}

	/**
	 * Gets the positions of critical points.
	 * 
	 * @return
	 */
	public List<Double> criticalPoints() {
		return new ArrayList<Double>(criticalPoints);
	}

	/**
	 * Gets the positions of local maxima.
	 * 
	 * @return
	 */
	public List<Double> maxima() {
		return new ArrayList<Double>(maxima);
	}

	/**
	 * Gets the positions of local minima.
	 * 
	 * @return
	 */
	public List<Double> minima() {
		return new ArrayList<Double>(minima);
	}

	/**
	 * Gets the x-coordinate of the absolute maximum.
	 * 
	 * @return
	 */
	public double maximum() {
		return maximum;
	}

	/**
	 * Gets the x-coordinate of the absolute minimum.
	 * 
	 * @return
	 */
	public double minimum() {
		return minimum;
	}

	/**
	 * Prints the x coordinates with their corresponding values to standard
	 * output.
	 * 
	 * @param xValues
	 */
	public void printPoints(final List<Double> xValues) {
		for (double x : xValues) {
			System.out.println("x=" + x + ", y=" + f.value(x));
		}
	}

	/**
	 * Prints the x coordinates with their corresponding values to standard
	 * output.
	 * 
	 * @param xValues
	 */
	public void printPoints(final double[] xValues) {
		for (double x : xValues) {
			System.out.println("x=" + x + ", y=" + f.value(x));
		}
	}

	/**
	 * Gets the positions of n significant points. These may be the critical
	 * points with the greatest y values, the sharpest peaks etc.
	 * 
	 * @param comparisonType
	 * @param samples
	 * @param minDistance
	 * @return
	 */
	public List<Double> significantPoints(final int comparisonType,
			final int samples, final double minDistance) {
		int n = samples;
		if (n < 1 || minDistance < 0d) {
			throw new RuntimeException();
		}
		/* Gets the local critical points */
		CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		List<Double> list;

		switch (comparisonType) {
		case CriticalPointComparator.MAXIMUM_Y:
			list = criticalPoints.maxima();
		case CriticalPointComparator.MINIMUM_Y:
			list = criticalPoints.minima();
		case CriticalPointComparator.SECOND_DERIVATIVE:
		case CriticalPointComparator.INCREASING_X:
		default:
			list = criticalPoints.criticalPoints();
		}

		/* Removes points too close to one another */
		double last = -minDistance - 1.0;
		List<Double> shortlist = new ArrayList<Double>(list);
		for (double x : list) {
			if (x < last + minDistance) {
				shortlist.remove(x);
			} else {
				last = x;
			}
		}

		// if (shortlist.size() < n) {
		// return new ArrayList<Double>();
		// }

		Collections.sort(shortlist, new CriticalPointComparator(f,
				comparisonType));

		/* Selects the n points of greatest value */
		if (n > shortlist.size()) {
			n = shortlist.size();
		}
		shortlist = shortlist.subList(0, n);
		Collections.sort(shortlist, new CriticalPointComparator(f,
				CriticalPointComparator.INCREASING_X));

		// if (ShapeDetector.debug) {
		// System.out
		// .println("SignificantPoints() :");
		// criticalPoints.printPoints(shortlist);
		// }
		return shortlist;
	}
}
