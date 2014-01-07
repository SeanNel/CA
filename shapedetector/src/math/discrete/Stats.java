package math.discrete;

import graphics.LineChartPanel;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.fitting.PolynomialFitter;
import org.apache.commons.math3.optim.ConvergenceChecker;
import org.apache.commons.math3.optim.PointVectorValuePair;
import org.apache.commons.math3.optim.SimpleVectorValueChecker;
import org.apache.commons.math3.optim.nonlinear.vector.MultivariateVectorOptimizer;
import org.apache.commons.math3.optim.nonlinear.vector.jacobian.GaussNewtonOptimizer;

public class Stats {

	/**
	 * Gets the mean value (average) of the function.
	 * 
	 * @param f
	 * @return
	 */
	public static double average(double[] f) {
		return DiscreteFunction.integrate(f) / f.length;
	}

	/**
	 * Finds x coordinate of the absolute minimum.
	 * 
	 * @param f
	 * @return
	 */
	public static int findValley(double[] f) {
		return findValley(f, 0, f.length);
	}

	/**
	 * Finds x coordinate of the absolute minimum in the specified domain.
	 * 
	 * @param f
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static int findValley(double[] f, int x1, int x2) {
		double minimum = Double.MAX_VALUE;
		int minimumPosition = 0;

		for (int x = x1; x < x2; x++) {
			double y = f[x];
			if (y < minimum) {
				minimum = y;
				minimumPosition = x;
			}
		}

		return minimumPosition;
	}

	/**
	 * Finds x coordinate of the absolute maximum.
	 * 
	 * @param f
	 * @return
	 */
	public static int findPeak(double[] f) {
		return findPeak(f, 0, f.length);
	}

	/**
	 * Finds x coordinate of the absolute maximum in the specified domain.
	 * 
	 * @param f
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static int findPeak(double[] f, int x1, int x2) {
		double maximum = Double.MIN_VALUE;
		int maximumPosition = 0;

		for (int x = x1; x < x2; x++) {
			double y = f[x];
			if (y > maximum) {
				maximum = y;
				maximumPosition = x;
			}
		}

		return maximumPosition;
	}

	/**
	 * Gets the maximum value of the function.
	 * 
	 * @param f
	 * @return
	 */
	public static double maximum(double[] f) {
		double maximum = 0.0;

		for (int x = 0; x < f.length; x++) {
			if (f[x] > maximum) {
				maximum = f[x];
			}
		}
		return maximum;
	}

	/**
	 * Gets the minimum value of the function.
	 * 
	 * @param f
	 * @return
	 */
	public static double minimum(double[] f) {
		double minimum = Double.MAX_VALUE;

		for (int x = 0; x < f.length; x++) {
			if (f[x] < minimum) {
				minimum = f[x];
			}
		}
		return minimum;
	}

	/**
	 * Gets the coordinates of a function's local maxima.
	 * 
	 * @param f
	 * @return
	 */
	public static List<Integer> maxima(double[] f) {
		/* For debugging */
		LineChartPanel.displayDifferentialData(f);

		if (f.length < 3) {
			throw new RuntimeException();
		}

		double[] df = f.clone();
		DiscreteFunction.differentiate(df);
		List<Integer> maxima = new ArrayList<Integer>();

		/* Prevents multiple maxima being picked up at eccentric turning points. */
		int lastCrit = Integer.MIN_VALUE;
		int delta = 1;

		// if (df[df.length - 1] > 0.0 && df[1] <= 0.0) {
		// maxima.add(0);
		// lastCrit = 0;
		// }

		for (int x = 0; x < df.length - 2; x++) {
			int h = x + 1;
			if (h >= df.length) {
				h = 0;
			}

			if (df[x] > 0.0 && df[h] <= 0.0 && lastCrit + delta < x) {
				maxima.add(x);
				lastCrit = x;
			}
		}

		/* For debugging, display a list of the found maxima. */
		 DiscreteFunction.displayPoints(f, maxima);

		return maxima;
	}

	/**
	 * Regresses the data distribution to an n'th polynomial.
	 * 
	 * @bug The graph is not perfectly periodic as it should be. TODO: loop
	 *      graph, then regress, then crop graph to solve periodicity problem.
	 * @param f
	 * @param n
	 * @return
	 */
	public static double[] regress(double[] f, int n) {
		int numPoints = f.length;

		/*
		 * -1 indicates that it should ignore absolute value differences and
		 * only look at relative differences of 1.0.
		 */
		ConvergenceChecker<PointVectorValuePair> checker = new SimpleVectorValueChecker(
				1.0, -1);
		MultivariateVectorOptimizer optimizer = new GaussNewtonOptimizer(
				checker);
		PolynomialFitter fitter = new PolynomialFitter(optimizer);
		for (int x = 0; x < numPoints; x++) {
			fitter.addObservedPoint(x, f[x]);
		}
		/*
		 * Attempts to make the regressed graph continuous around the start and
		 * end.
		 */
		/* Makes the graph repeat once. */
		// for (int x = 0; x < numPoints; x++) { // / 2
		// fitter.addObservedPoint(x + numPoints, f[x]);
		// }
		/* Adds a copy of the last point on the graph before the 1st point. */
		fitter.addObservedPoint(-1, f[numPoints - 1]);
		/* Adds a copy of the 1st point on the graph after the last point. */
		// fitter.addObservedPoint(numPoints + 1, f[0]);

		/* Further optimization may be done here with the initial guess. */
		double[] guess = new double[n];
		guess[0] = f[0];
		for (int i = 1; i < n; i++) {
			guess[i] = 0;
		}

		double[] coefficients = fitter.fit(guess);
		double[] g = DiscreteFunction.plotPolynomial(coefficients, numPoints);

		/*
		 * Displays the graph, repeated once, to demonstrate its continuity
		 * around the ends.
		 */
		// double[] g = plotPolynomial(coefficients, numPoints * 2);
		// f = loop(f, 2);
		// LineChartPanel.displayData(f, "f(x)", g, "g(x)");

		return g;
	}

	public static void normalize(double[] f) {
		double norm = Math.sqrt(DiscreteFunction.dotProduct(f, f));
		DiscreteFunction.times(f, 1.0 / norm);
	}

	public static void normalizeTo(double[] f, double c) {
		double norm = c * (double) f.length;
		DiscreteFunction.times(f, 1.0 / norm);
	}

}
