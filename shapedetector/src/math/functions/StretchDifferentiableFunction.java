package math.functions;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;

public class StretchDifferentiableFunction extends StretchFunction implements
		UnivariateDifferentiableFunction {

	public StretchDifferentiableFunction(
			final UnivariateDifferentiableFunction f, final double x0,
			final double x1, final double c0, final double c1) {
		super(f, x0, x1, c0, c1);
	}

	@Override
	public DerivativeStructure value(final DerivativeStructure t)
			throws DimensionMismatchException {
		return ((UnivariateDifferentiableFunction) f).value(t);
	}
}
