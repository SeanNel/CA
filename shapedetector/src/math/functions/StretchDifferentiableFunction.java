package math.functions;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class StretchDifferentiableFunction extends StretchFunction implements
		UnivariateDifferentiableFunction {

	public StretchDifferentiableFunction(
			final UnivariateDifferentiableFunction f, final double x0,
			final double x1, double c0, double c1) {
		super(f, x0, x1, c0, c1);
	}

	@Override
	public DerivativeStructure value(DerivativeStructure t)
			throws DimensionMismatchException {
		return ((UnivariateDifferentiableFunction) f).value(t);
	}
}
