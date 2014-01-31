package math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.util.MathUtils;

/**
 * Creates an interface to a function that is periodic over a given domain. That
 * is, x-values that are outside of the function's domain are mapped back to
 * values inside the domain before returning the y-value.
 * <p>
 * It also provides an efficient mechanism for rotating the graph (shifting
 * along the x-axis, wrapping around the end).
 * 
 * @author Sean
 */
public class PeriodicFunction implements UnivariateFunction, Periodic {
	protected final UnivariateFunction f;
	protected final double x0;
	protected final double x1;
	protected double rotation;

	public PeriodicFunction(final UnivariateFunction f, final double x0,
			final double x1, final double rotation) {
		if (f == null || x1 <= x0) {
			throw new RuntimeException();
		}
		this.f = f;
		this.x0 = x0;
		this.x1 = x1;
		this.rotation = rotation;
	}

	public PeriodicFunction(UnivariateFunction f, double x0, double x1) {
		this(f, x0, x1, 0d);
	}

	public void rotate(final double x) {
		rotation += x;
	}

	@Override
	public double value(final double x) {
		double h = x;
		h += rotation;
		double period = x1 - x0;
		h = MathUtils.reduce(h, period, 0);
		return f.value(h);
	}

}
