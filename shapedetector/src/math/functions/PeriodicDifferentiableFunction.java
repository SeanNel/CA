package math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.FiniteDifferencesDifferentiator;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.analysis.differentiation.UnivariateFunctionDifferentiator;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class PeriodicDifferentiableFunction extends PeriodicFunction implements
		UnivariateDifferentiableFunction {
	protected final static UnivariateFunctionDifferentiator differentiator = new FiniteDifferencesDifferentiator(
			2, 1);

	public PeriodicDifferentiableFunction(UnivariateDifferentiableFunction f,
			double x0, double x1) {
		super(f, x0, x1);
	}

	public PeriodicDifferentiableFunction(UnivariateFunction f, double x0,
			double x1) {
		super(differentiator.differentiate(f), x0, x1);
	}

	@Override
	public DerivativeStructure value(DerivativeStructure t)
			throws DimensionMismatchException {
		return ((UnivariateDifferentiableFunction) f).value(t);
	}

}
