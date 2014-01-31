package math;

import java.util.Arrays;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.exception.NoDataException;
import org.apache.commons.math3.exception.NonMonotonicSequenceException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.util.MathArrays;

/**
 * A step function.
 * 
 * @author Sean
 */
public class DiscreteFunction implements UnivariateFunction { // UnivariateDifferentiableFunction
	/** Abscissae. */
	protected final double[] abscissae;
	/** Ordinates. */
	protected final double[] ordinates;

	/**
	 * Builds a step function from a list of arguments and the corresponding
	 * values. Specifically, returns the function h(x) defined by
	 * 
	 * <pre>
	 * <code>
	 * h(x) = y[0] for all x < x[1]
	 *        y[1] for x[1] <= x < x[2]
	 *        ...
	 *        y[y.length - 1] for x >= x[x.length - 1]
	 * </code>
	 * </pre>
	 * 
	 * The value of {@code x[0]} is ignored, but it must be strictly less than
	 * {@code x[1]}.
	 * 
	 * @param x
	 *            Domain values where the function changes value.
	 * @param y
	 *            Values of the function.
	 * @throws NonMonotonicSequenceException
	 *             if the {@code x} array is not sorted in strictly increasing
	 *             order.
	 * @throws NullArgumentException
	 *             if {@code x} or {@code y} are {@code null}.
	 * @throws NoDataException
	 *             if {@code x} or {@code y} are zero-length.
	 * @throws DimensionMismatchException
	 *             if {@code x} and {@code y} do not have the same length.
	 */
	public DiscreteFunction(double[] x, double[] y)
			throws NullArgumentException, NoDataException,
			DimensionMismatchException, NonMonotonicSequenceException {
		if (x == null || y == null) {
			throw new NullArgumentException();
		}
		if (x.length == 0 || y.length == 0) {
			throw new NoDataException();
		}
		if (y.length != x.length) {
			throw new DimensionMismatchException(y.length, x.length);
		}
		MathArrays.checkOrder(x);

		abscissae = MathArrays.copyOf(x);
		ordinates = MathArrays.copyOf(y);
	}

	/**
	 * Creates a function mapping each y value to an x value, from 0 to n.
	 * 
	 * @param y
	 */
	public DiscreteFunction(double[] y) {
		this(getAbscissae(0, y.length, y.length), y);
	}

	/**
	 * Creates a function mapping each y value to an x value, spaced equally
	 * from x0 to x1.
	 * 
	 * @param y
	 * @param x0
	 * @param x1
	 */
	public DiscreteFunction(double[] y, double x0, double x1) {
		this(getAbscissae(x0, x1, y.length), y);
	}

	/** {@inheritDoc} */
	@Override
	public double value(double x) {
		int index = Arrays.binarySearch(abscissae, x);
		double fx = 0;

		if (index < -1) {
			// "x" is between "abscissae[-index-2]" and "abscissae[-index-1]".
			fx = ordinates[-index - 2];
		} else if (index >= 0) {
			// "x" is exactly "abscissae[index]".
			fx = ordinates[index];
		} else {
			// Otherwise, "x" is smaller than the first value in "abscissae"
			// (hence the returned value should be "ordinate[0]").
			fx = ordinates[0];
		}

		return fx;
	}

	// /** {@inheritDoc} */
	// @Override
	// public DerivativeStructure value(DerivativeStructure t)
	// throws DimensionMismatchException {
	// if (t.getOrder() > 1) {
	// throw new RuntimeException();
	// }
	//
	// double x = Math.floor(t.getReal());
	// int index = Arrays.binarySearch(abscissae, x);
	// if (index + 1 > abscissae.length) {
	// index = abscissae.length;
	// }
	// double h = abscissae[index + 1];
	//
	// double dx = x - h;
	// double dy = ordinates[index + 1] - ordinates[index]; // value(x);
	//
	// DerivativeStructure result = new DerivativeStructure(
	// t.getFreeParameters(), t.getOrder(), dx / dy);
	// return result;
	// }

	public int size() {
		return abscissae.length;
	}

	public double[] getAbscissae() {
		return abscissae;
	}

	public double[] getOrdinates() {
		return ordinates;
	}

	public static double[] getAbscissae(double x0, double x1, int n) {
		if (x1 <= x0 || n <= 0) {
			throw new RuntimeException();
		}
		double[] abscissae = new double[n];
		// double delta = (x1 - x0) / (double) n;
		// double x = x0;
		double period = x1 - x0;
		for (int i = 0; i < n; i++) {
			// x += delta;
			abscissae[i] = x0 + ((double) i * period / (double) n);
		}
		return abscissae;
	}
}
