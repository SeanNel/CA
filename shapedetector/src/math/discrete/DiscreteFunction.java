package math.discrete;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * A step function used to approximate a continuous, differentiable univariate
 * function based on discrete data points.
 * 
 * @author Sean
 */
public abstract class DiscreteFunction<V extends Number> implements
		UnivariateFunction {
	/** y values corresponding to x = 0, 1, 2, ... n */
	protected List<V> real;

	public static final int RESIZE_STRETCH = 0;
	public static final int RESIZE_CROP = 1;
	public static final int RESIZE_LOOP = 2;

	protected V padding;

	protected DiscreteFunction() {
	}

	/**
	 * Constructor.
	 * 
	 * @param n
	 *            Number of discrete x-values in the range.
	 */
	public DiscreteFunction(int n) {
		real = new ArrayList<V>(n);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param f
	 */
	public DiscreteFunction(DiscreteFunction<V> f) {
		real = new ArrayList<V>(f.real);
	}

	/**
	 * Constructor.
	 * 
	 * @param array
	 */
	public DiscreteFunction(V[] array) {
		real = new ArrayList<V>(array.length);
		for (V v : array) {
			real.add(v);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param list
	 */
	public DiscreteFunction(List<V> list) {
		real = new ArrayList<V>(list);
	}

	/**
	 * Constructor. Given the polynomial coefficients, plots the y-values
	 * against x-values from 0 to n.
	 * <p>
	 * Shortcut for creating a PolynomialFunction and gettign a DiscreteFunction
	 * from that.
	 * 
	 * @param coefficients
	 * @param n
	 */
	@SuppressWarnings("unchecked")
	public DiscreteFunction(double[] coefficients, int n) {
		real = new ArrayList<V>(n);
		int p = coefficients.length;
		for (int x = 0; x < n; x++) {
			Double y = 0.0;
			for (int a = 0; a < p; a++) {
				y += coefficients[a] * Math.pow(x, a);
			}
			real.add((V) y);
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * (Gets the real component.)
	 */
	@Override
	public double value(double x) {
		int n = real.size();
		int x0 = (int) Math.floor(x);
		while (x0 < 0) {
			x0 += n;
		}
		while (x0 >= n) {
			x0 -= n;
		}

		return (Double) real.get(x0);
	}

	/**
	 * Gets the size of the domain (number of steps in the function).
	 * 
	 * @return
	 */
	public int size() {
		return real.size();
	}

	/**
	 * Sets the number of discrete x-values in the domain.
	 * 
	 * @param n
	 * @param type
	 *            The method used to transform data to the new domain.
	 */
	public void resize(int n, int type) {
		List<V> f = real;
		real = new ArrayList<V>();

		if (f.size() == 0) {
			type = RESIZE_CROP;
		}

		switch (type) {
		case RESIZE_STRETCH:
			for (int x = 0; x < n; x++) {
				double a = (double) x * (double) f.size() / (double) n;
				real.add(f.get((int) Math.floor(a)));
			}
			break;
		case RESIZE_CROP:
			for (int x = 0; x < n && x < f.size(); x++) {
				real.add(f.get(x));
			}
			for (int x = f.size(); x < n; x++) {
				real.add(padding);
			}
			break;
		case RESIZE_LOOP:
			// todo
			// int n = (int) ((double) f.length * factor);
			// double[] f1 = new double[n];
			//
			// int x = 0;
			// for (int i = 0; i < n; i++) {
			// x++;
			// if (x >= f.length) {
			// x = 0;
			// }
			// f1[i] = f[x];
			// }
			break;
		}
	}

	// public static void normalize(double[] f) {
	// double norm = Math.sqrt(DiscretePeriodic.dotProduct(f, f));
	// DiscretePeriodic.times(f, 1.0 / norm);
	// }
	//
	// public static void normalizeTo(double[] f, double c) {
	// double norm = c * (double) f.length;
	// DiscretePeriodic.times(f, 1.0 / norm);
	// }

	public Iterator<V> iterator() {
		return real.iterator();
	}
}
