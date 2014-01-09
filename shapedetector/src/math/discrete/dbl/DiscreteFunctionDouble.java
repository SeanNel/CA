package math.discrete.dbl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;

import exceptions.CAException;
import math.discrete.DiscreteFunction;

public class DiscreteFunctionDouble extends DiscreteFunction<Double> {

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDouble(int n) {
		super(n);
		padding = 0.0;
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDouble(DiscreteFunction<Double> f) {
		super(f);
		padding = 0.0;
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDouble(double[] real) {
		this(real.length);
		this.real = new ArrayList<Double>(real.length);
		for (double x : real) {
			this.real.add(x);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDouble(List<Double> list) {
		super(list);
		padding = 0.0;
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDouble(double[] coefficients, int n) {
		super(coefficients, n);
		padding = 0.0;
	}

	@Override
	public DiscreteFunctionDouble clone() {
		return new DiscreteFunctionDouble(this);
	}

	/**
	 * {@inheritDoc}
	 */
	public void add(double c) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) + c);
		}
	}

	/**
	 * Adds the functions together.
	 * 
	 * @param f
	 */
	public void add(UnivariateFunction f) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) + f.value(x));
		}
	}

	/**
	 * Takes the difference of the functions.
	 * 
	 * @param f
	 */
	public void subtract(DiscreteFunctionDoublePeriodic f) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) - f.value(x));
		}
	}

	/**
	 * Multiplies by a constant.
	 * 
	 * @param c
	 */
	public void times(double c) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) * c);
		}
	}

	/**
	 * Divides by a constant.
	 * 
	 * @param c
	 */
	public void divide(double c) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) / c);
		}
	}

	/**
	 * Computes the absolute value of the function.
	 * 
	 * @param f
	 */
	public void absoluteValue() {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, Math.abs(real.get(x) ));
		}
	}

	/**
	 * Gets the integral over the entire domain.
	 * 
	 * @param f
	 * @return
	 */
	public double integrate() {
		Double integral = 0.0;
		for (Double y : real) {
			integral += y;
		}
		return integral;
	}

	/**
	 * Integrates over the specified domain.
	 * 
	 * @param x1
	 * @param x2
	 * @return
	 */
	public double integrate(int x1, int x2) {
		Double integral = 0.0;
		for (int x = x1; x < x2; x++) {
			integral += real.get(x);
		}
		return integral;
	}

	/**
	 * Takes the power of the function to a constant.
	 * 
	 * @param c
	 */
	public void pow(double c) {
		for (Double y : real) {
			y = Math.pow(y, c);
		}
	}

	/**
	 * Multiples each y value in this function by the corresponding value in f.
	 * 
	 * @param f
	 * @throws DimensionMismatchException
	 */
	public void pointwiseProduct(DiscreteFunction<Double> f)
			throws DimensionMismatchException {
		int n = real.size();
		if (n != f.size()) {
			throw new DimensionMismatchException(n, f.size());
		}

		int x = 0;
		for (Double y : real) {
			y *= f.value(x++);
		}
	}

	/**
	 * Gets the dot (inner/scalar) product.
	 * 
	 * @param f
	 * @return
	 * @throws DimensionMismatchException
	 */
	public double dotProduct(DiscreteFunction<Double> f)
			throws DimensionMismatchException {
		int n = real.size();
		if (n != f.size()) {
			throw new DimensionMismatchException(n, f.size());
		}
		DiscreteFunctionDouble g = new DiscreteFunctionDouble(this);
		g.pointwiseProduct(f);
		return g.integrate();
	}

	/**
	 * Gets the derivate of this function.
	 * 
	 * @return
	 */
	public DiscreteFunctionDouble derivative() {
		return new DiscreteFunctionDouble(differentiate(toArray()));
	}

	/**
	 * Differentiates the function.
	 * <p>
	 * f'(x) = [f(x + dx) - f(x)] / dx
	 */
	public static double[] differentiate(double[] f) {
		// for (int x = 0; x < real.length; x++) {
		// derivative.real[x] = derivative.value(x + 1);
		// }

		double f0 = f[0];
		for (int x = 0; x < f.length - 1; x++) {
			f[x] = f[x + 1] - f[x];
		}
		int x = f.length - 1;
		f[x] = f0 - f[x];
		return f;
	}

	/**
	 * For debugging. Prints out the x values and their corresponding y values.
	 * 
	 * @param f
	 * @param points
	 */
	public void printValues(List<Integer> points) {
		System.out.println("***");
		for (Integer x : points) {
			System.out.println("x=" + x + ", y=" + real.get(x));
		}
	}

	/**
	 * Returns an array with entries mapping x = 0, 1, ... n to y values.
	 * 
	 * @return
	 */
	public double[] toArray() {
		int n = real.size();
		double[] array = new double[n];
		Iterator<Double> iterator = real.iterator();

		for (int i = 0; i < n; i++) {
			array[i] = iterator.next();
		}
		return array;
	}

	/**
	 * Stores entries mapping x = 0, 1, ... n to y values in the array given as
	 * parameter. Creates a new array if the parameter is null.
	 * 
	 * @throws CAException
	 *             if the array is of the wrong size.
	 * @return
	 */
	public double[] toArray(double[] array) throws CAException {
		int n = real.size();
		if (array == null) {
			array = new double[n];
		} else if (array.length != n) {
			throw new CAException();
		}
		Iterator<Double> iterator = real.iterator();

		for (int i = 0; i < n; i++) {
			array[i] = iterator.next();
		}
		return array;
	}

	/**
	 * Inserts the value at the end of the function.
	 * 
	 * @param c
	 */
	public void append(double c) {
		real.add(c);
	}

	/**
	 * Gets the mean value (average) of the function.
	 * 
	 * @return
	 */
	public double mean() {
		double sum = 0.0;
		for (double x : real) {
			sum += x;
		}
		return sum / real.size();
	}

	/**
	 * Finds x coordinate of the (1st) absolute minimum.
	 * 
	 * @return
	 */
	public int minimumX() {
		return minimumX(0, real.size());
	}

	/**
	 * Finds x coordinate of the (1st) absolute minimum in the specified domain.
	 * 
	 * @param x1
	 * @param x2
	 * @return
	 */
	public int minimumX(int x1, int x2) {
		double minimum = Double.MAX_VALUE;
		int minimumPosition = 0;

		for (int x = x1; x < x2; x++) {
			double y = real.get(x);
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
	 * @return
	 */
	public int maximumX() {
		return maximumX(0, real.size());
	}

	/**
	 * Finds x coordinate of the absolute maximum in the specified domain.
	 * 
	 * @param x1
	 * @param x2
	 * @return
	 */
	public int maximumX(int x1, int x2) {
		double maximum = Double.MIN_VALUE;
		int maximumPosition = 0;

		for (int x = x1; x < x2; x++) {
			double y = real.get(x);
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
	public double maximum() {
		return real.get(maximumX());
	}

	/**
	 * Gets the minimum value of the function.
	 * 
	 * @return
	 */
	public double minimum() {
		return real.get(minimumX());
	}

	/**
	 * Gets the coordinates of a function's local maxima.
	 * 
	 * @param f
	 * @return
	 */
	public List<Integer> maxima() {
		/* For debugging */
		// graphics.LineChartFrame.displayDifferentialData(this);

		int n = real.size();
		if (n < 3) {
			throw new RuntimeException();
		}

		DiscreteFunctionDouble df = derivative();
		List<Integer> maxima = new ArrayList<Integer>();

		/* Prevents multiple maxima being picked up at eccentric turning points. */
		int lastCrit = Integer.MIN_VALUE;
		int delta = 2;

		// if (df[df.length - 1] > 0.0 && df[1] <= 0.0) {
		// maxima.add(0);
		// lastCrit = 0;
		// }
		Iterator<Double> xIterator = df.real.iterator();
		Iterator<Double> hIterator = df.real.iterator();
		hIterator.next(); /* h = x + 1 */

		for (int x = 0; x < n; x++) {
			if (!hIterator.hasNext()) {
				hIterator = df.real.iterator();
			}

			Double fx = xIterator.next();
			Double fh = hIterator.next();
			if (fx > 0.0 && fh <= 0.0 && lastCrit + delta < x) {
				maxima.add(x);
				lastCrit = x;
			}
		}

		/* For debugging, display a list of the found maxima. */
		// printValues(maxima);

		return maxima;
	}

	/**
	 * Ensures that changes to the values in this function do not affect
	 * anything else.
	 */
	// public void seal() {
	// double[] r = toArray();
	// real = new ArrayList<Double>(r.length);
	// for (double x : r) {
	// real.add(x);
	// }
	// }
}
