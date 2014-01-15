package math.utils;

import java.util.ArrayList;
import java.util.List;

import math.functions.Differential;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

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
	public CriticalPoints(UnivariateDifferentiableFunction f, double x0,
			double x1) {
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

	public double value(double x) {
		return f.value(x);
	}

	public DerivativeStructure value(DerivativeStructure t) {
		return f.value(t);
	}

	protected static List<Double> filterSolutions(double[] solutions, double delta) {
		double lastX = Double.MIN_VALUE;
		int n = solutions.length;
		List<Double> criticalPoints = new ArrayList<Double>(n);
		for (int i = 0; i < n; i++) {
			double x = solutions[i];
			if (x < lastX + delta) {
				continue;
			} else {
				lastX = x;
			}
			criticalPoints.add(x);
		}
		return criticalPoints;
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
	public void printPoints(List<Double> xValues) {
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
	public void printPoints(double[] xValues) {
		for (double x : xValues) {
			System.out.println("x=" + x + ", y=" + f.value(x));
		}
	}
}
