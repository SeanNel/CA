package math.discrete;

import java.util.ArrayList;
import java.util.List;

/**
 * A discrete function of complex values.
 * 
 * @author Sean
 */
public abstract class DiscreteFunctionComplex<V extends Number> extends
		DiscreteFunction<V> {
	/** Imaginary component of y values corresponding to x = 0, 1, 2, ... n */
	protected List<V> imaginary;

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionComplex(int n) {
		super(n);
		imaginary = new ArrayList<V>(n);
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionComplex(DiscreteFunction<V> f) {
		super(f);
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionComplex(DiscreteFunctionComplex<V> f) {
		super(f);
		imaginary = new ArrayList<V>(f.imaginary);
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionComplex(V[] array) {
		super(array);
	}

	/**
	 * {@inheritDoc}
	 */
	public DiscreteFunctionComplex(double[] coefficients, int n) {
		super(coefficients, n);
		imaginary = new ArrayList<V>(n);
	}

	public double valueImaginary(double x) {
		return 0.0;
	}
}
