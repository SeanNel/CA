package math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateFunctionDifferentiator;
import org.apache.commons.math3.exception.DimensionMismatchException;

/**
 * Wrapper for function differentials. Allows passing the differentiation of a
 * function to methods that operate by value(double).
 * 
 * @see http ://commons.apache.org/proper/commons-math/userguide/analysis.html
 *      #a4.7_Differentiation
 */
public class Differential implements UnivariateDifferentiableFunction {
	final protected UnivariateDifferentiableFunction f;

	public Differential(final UnivariateDifferentiableFunction f) {
		if (f == null) {
			throw new RuntimeException();
		}
		this.f = f;
	}

	/**
	 * Gets the differentiable of a UnivariateFunction function that does not
	 * imlement UnivariateDifferentiableFunction.
	 * 
	 * @param f
	 * @param stepSize
	 */
	public Differential(final UnivariateFunction f, final double stepSize) {
		if (f == null || stepSize <= 0) {
			throw new RuntimeException();
		}

		double tLower = Double.MIN_VALUE;
		double tUpper = Double.MAX_VALUE;
		int nbPoints = 3; // 8

		UnivariateFunctionDifferentiator differentiator = new FiniteDifferencesDifferentiator(
				nbPoints, stepSize, tLower, tUpper);
		this.f = differentiator.differentiate(f);
	}

	public UnivariateDifferentiableFunction getDifferential() {
		return f;
	}

	@Override
	public double value(final double xRealValue) {
		int params = 1;
		int order = 1;
		DerivativeStructure x = new DerivativeStructure(params, order, 0,
				xRealValue);
		DerivativeStructure y = f.value(x);

		return y.getPartialDerivative(1);
	}

	@Override
	public DerivativeStructure value(final DerivativeStructure t)
			throws DimensionMismatchException {
		int params = 1;
		int order = t.getOrder() + 1;
		DerivativeStructure x = new DerivativeStructure(params, order, 0,
				t.getReal());
		DerivativeStructure y = f.value(x);

		return y;
	}
}
