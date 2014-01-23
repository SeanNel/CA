package math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Maps a function over a domain [x0,x1] to the target domain [c0,c1].
 * 
 * @author Sean
 * 
 */
public class StretchFunction implements UnivariateFunction {
	protected final UnivariateFunction f;
	protected final double x0;
	protected final double x1;

	protected double c0;
	protected double c1;

	public StretchFunction(final UnivariateFunction f, final double x0,
			final double x1, final double c0, final double c1) {
		this.f = f;
		this.x0 = x0;
		this.x1 = x1;
		this.c0 = c0;
		this.c1 = c1;
		if (x0 >= x1) {
			throw new RuntimeException("x0 >= x1");
		}
		if (c0 >= c1) {
			throw new RuntimeException("c0 >= c1");
		}
	}

	public void stretch(final double c0, final double c1) {
		this.c0 = c0;
		this.c1 = c1;
	}

	@Override
	public double value(final double x) {
		double period0 = x1 - x0;
		double period1 = c1 - c0;

		return f.value(x * period0 / period1);
	}

}
