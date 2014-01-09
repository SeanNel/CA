package math.discrete.dbl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math.discrete.DiscreteFunction;
import math.discrete.DiscreteFunctionComplex;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.exception.DimensionMismatchException;

import exceptions.CAException;

/**
 * A discrete function of complex values. This class is nowhere near complete.
 * 
 * @author Sean
 */
public class DiscreteFunctionDoubleComplex extends
		DiscreteFunctionComplex<Double> {

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDoubleComplex(int n) {
		super(n);
		padding = 0.0;
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDoubleComplex(DiscreteFunction<Double> f) {
		super(f);
		padding = 0.0;
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDoubleComplex(DiscreteFunctionComplex<Double> f) {
		super(f);
		padding = 0.0;
	}

	/**
	 * Constructor.
	 * 
	 * @param array
	 */
	public DiscreteFunctionDoubleComplex(double[] real) {
		this(real.length);
		for (double x : real) {
			this.real.add(x);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param array
	 */
	public DiscreteFunctionDoubleComplex(double[] real, double[] imaginary) {
		this(real);
		for (double x : imaginary) {
			this.imaginary.add(x);
		}
	}

	/**
	 * Constructor.
	 * 
	 * @param array
	 */
	public DiscreteFunctionDoubleComplex(Complex[] complex) {
		this(complex.length);
		for (Complex x : complex) {
			this.real.add(x.getReal());
			this.imaginary.add(x.getImaginary());
		}
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionDoubleComplex(double[] coefficients, int n) {
		super(coefficients, n);
		padding = 0.0;
	}

	@Override
	public DiscreteFunctionDoubleComplex clone() {
		return new DiscreteFunctionDoubleComplex(this);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int size() {
		return real.size();
	}

	/**
	 * Inserts the value to the end of the function.
	 * 
	 * @param r
	 */
	public void append(double r) {
		real.add(r);
		imaginary.add(0.0);
	}

	/**
	 * Inserts the value to the end of the function.
	 * 
	 * @param r
	 * @param i
	 */
	public void append(double r, double i) {
		real.add(r);
		imaginary.add(i);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * (Gets the imaginary component.)
	 */
	public double valueImaginary(double x) {
		int n = imaginary.size();
		int x0 = (int) Math.floor(x);
		while (x0 < 0) {
			x0 += n;
		}
		while (x0 > n) {
			x0 -= n;
		}

		return (Double) imaginary.get(x0);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param f
	 */
	public void add(DiscreteFunctionDoubleComplex f) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) - f.value(x));
		}
		for (int x = 0; x < imaginary.size(); x++) {
			imaginary.set(x, imaginary.get(x) + f.value(x));
		}
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @param f
	 */
	public void add(double r, double i) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) + r);
		}
		for (int x = 0; x < imaginary.size(); x++) {
			imaginary.set(x, imaginary.get(x) + i);
		}
	}

	/**
	 * Multiplies by a constant.
	 * 
	 * @param c
	 * @throws CAException
	 */
	public void times(double r, double i) throws CAException {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, real.get(x) * r);
		}
		for (int x = 0; x < imaginary.size(); x++) {
			imaginary.set(x, imaginary.get(x) * i);
		}
	}

	/**
	 * Computes the absolute value of the function.
	 * 
	 * @param f
	 */
	public void absoluteValue() {
		int n = real.size();
		Iterator<Double> rIterator = real.iterator();
		Iterator<Double> iIterator = imaginary.iterator();
		List<Double> values = new ArrayList<Double>(n);

		while (iIterator.hasNext()) {
			Double r = rIterator.next();
			Double i = iIterator.next();
			Double x = Math.sqrt(r * r + i * i);

			values.add(x);
		}

		real = values;
		imaginary = new ArrayList<Double>(n);
	}

	/**
	 * Takes the power of the function to a constant.
	 * 
	 * @param c
	 */
	public void pow(double c) {
		for (int x = 0; x < real.size(); x++) {
			real.set(x, Math.pow(real.get(x), c));
		}
		for (int x = 0; x < imaginary.size(); x++) {
			imaginary.set(x, Math.pow(imaginary.get(x), c));
		}
	}

	public void pointwiseProduct(DiscreteFunction<Double> f)
			throws DimensionMismatchException {
		int n = real.size();
		if (n != f.size()) {
			throw new DimensionMismatchException(n, f.size());
		}

		for (int x = 0; x < real.size(); x++) {
			real.set(x, Math.pow(real.get(x), f.value(x)));
		}
	}

	public void pointwiseProduct(DiscreteFunctionComplex<Double> f)
			throws DimensionMismatchException {
		int n = real.size();
		if (n != f.size()) {
			throw new DimensionMismatchException(n, f.size());
		}

		for (int x = 0; x < real.size(); x++) {
			real.set(x, Math.pow(real.get(x), f.value(x)));
		}
		for (int x = 0; x < imaginary.size(); x++) {
			imaginary.set(x, Math.pow(imaginary.get(x), f.valueImaginary(x)));
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
		DiscreteFunctionDoubleComplex g = new DiscreteFunctionDoubleComplex(
				this);
		g.pointwiseProduct(f);
		DiscreteFunctionDouble h = new DiscreteFunctionDouble(g);
		return h.integrate();
	}

	/**
	 * {@inheritDoc}
	 */
	public void toArray(double[] real, double[] imaginary) throws CAException {
		int n = this.real.size();
		if (real.length != n || imaginary.length != n) {
			throw new CAException();
		}
		Iterator<Double> r = this.real.iterator();
		Iterator<Double> i = this.imaginary.iterator();

		for (int x = 0; x < n; x++) {
			real[x] = r.next();
			imaginary[x] = i.next();
		}
	}
}
