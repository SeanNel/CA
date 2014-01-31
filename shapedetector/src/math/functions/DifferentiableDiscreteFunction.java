package math.functions;

import java.util.Arrays;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;

import math.DiscreteFunction;

/**
 * Layer for differentiating a discrete function.
 * 
 * @author Sean
 */
public class DifferentiableDiscreteFunction implements
		UnivariateDifferentiableFunction {
	protected DiscreteFunction f;

	public DifferentiableDiscreteFunction(DiscreteFunction f) {
		this.f = f;
	}

	/** {@inheritDoc} */
	@Override
	public DerivativeStructure value(DerivativeStructure t)
			throws DimensionMismatchException {
		double[] derivatives = new double[t.getOrder() + 1];
		derivatives[0] = f.value(t.getReal());

		if (t.getOrder() == 1) {
			double[] abscissae = f.getAbscissae();
			double[] ordinates = f.getOrdinates();

			int xIndex = getIndex(t.getReal());
			int hIndex = xIndex + 1;
			/* Assumes f is periodic */
			if (hIndex >= abscissae.length) {
				hIndex = 0;
			}
			double x = abscissae[xIndex];
			double h = abscissae[hIndex];
			double dx = Double.MAX_VALUE;

			if (hIndex == 0) {
				if (xIndex == 0) {
					throw new RuntimeException();
				} else if (h == 0) {
					/*
					 * The domain starts at 0, so assume that the distance
					 * between the last two elements is the same as the distance
					 * (wrapped around the end) between the ultimate element and
					 * the first one.
					 */
					dx = abscissae[xIndex] - abscissae[xIndex - 1];
				} else {
					dx = h;
				}
			} else {
				dx = h - x;
			}

			double dy = ordinates[hIndex] - ordinates[xIndex]; // value(x);
			derivatives[1] = dy / dx;
		} else {
			throw new RuntimeException("Cannot take derivative of order > 1");
		}

		DerivativeStructure result = new DerivativeStructure(
				t.getFreeParameters(), t.getOrder(), derivatives);
		return result;
	}

	protected int getIndex(double x) {
		int index = Arrays.binarySearch(f.getAbscissae(), x);

		if (index < -1) {
			// "x" is between "abscissae[-index-2]" and "abscissae[-index-1]".
			return -index - 2;
		} else if (index >= 0) {
			// "x" is exactly "abscissae[index]".
			return index;
		} else {
			// Otherwise, "x" is smaller than the first value in "abscissae"
			// (hence the returned value should be "ordinate[0]").
			return 0;
		}
	}

	@Override
	public double value(double x) {
		return f.value(x);
	}
}
