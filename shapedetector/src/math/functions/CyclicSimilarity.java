package math.functions;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;
import org.apache.commons.math3.exception.DimensionMismatchException;
import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.util.FastMath;

/**
 * Computes the similarity (mean squared differences) between two functions as a
 * function of phase shift. Treats functions as periodic.
 * <p>
 * is this the same as covariance?
 * */
public class CyclicSimilarity implements UnivariateDifferentiableFunction {
	protected final UnivariateFunction f1;
	protected final UnivariateFunction f2;
	protected final double x0;
	protected final double x1;
	protected final double delta;

	final double normalizationFactor;

	public CyclicSimilarity(UnivariateDifferentiableFunction f1,
			UnivariateDifferentiableFunction f2, double delta, double x0,
			double x1) {
		this.f1 = f1;
		this.f2 = f2;
		this.delta = delta;
		this.x0 = x0;
		this.x1 = x1;

		// graphics.LineChartFrame.displayData(x0, x1, f1, f2);

		// CriticalPoints criticalPoints1 = new CriticalPoints(f1, x0, x1);
		// CriticalPoints criticalPoints2 = new CriticalPoints(f2, x0, x1);
		//
		// double max = FastMath.max(f1.value(criticalPoints1.maximum()),
		// f2.value(criticalPoints2.maximum()));
		// double min = FastMath.min(f1.value(criticalPoints1.minimum()),
		// f2.value(criticalPoints2.minimum()));

		int n = (int) ((x1 - x0) / delta);
		double[] samples1 = FunctionUtils.sample(f1, x0, x1, n);
		double[] samples2 = FunctionUtils.sample(f2, x0, x1, n);
		double max = FastMath.max(StatUtils.max(samples1),
				StatUtils.max(samples2));
		double min = FastMath.min(StatUtils.min(samples1),
				StatUtils.min(samples2));

		normalizationFactor = (max - min) * (max - min);
		// if (normalizationFactor == 0) {
		// throw new RuntimeException();
		// }
	}

	@Override
	public double value(double x) {
		if (normalizationFactor == 0) {
			return 0.0;
		}
		double sum = 0.0;

		PeriodicFunction f2 = new PeriodicFunction(this.f2, x0, x1);
		f2.rotate(x);

		int n = 0;
		for (double i = x0; i < x1; i += delta) {
			double d = f1.value(i) - f2.value(i);
			sum += normalizationFactor - d * d;
			n++;
		}

		// double n = (int) ((x1 - x0) / delta);
		double y = sum / (double) n / normalizationFactor;
		if (y < 0.0) {
			return 0.0;
		}
		return y;
	}

	@Override
	public DerivativeStructure value(DerivativeStructure t)
			throws DimensionMismatchException {
		if (t.getOrder() > 1) {
			throw new RuntimeException();
		}
		double x = t.getReal();
		double h = x + delta;
		double dxdy = value(x + h) / delta;
		DerivativeStructure result = new DerivativeStructure(
				t.getFreeParameters(), t.getOrder(), dxdy);
		return result;
	}
}
