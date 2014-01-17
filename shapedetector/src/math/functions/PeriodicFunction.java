package math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class PeriodicFunction implements UnivariateFunction, Periodic {
	protected final UnivariateFunction f;
	protected final double x0;
	protected final double x1;
	protected double rotation;

	public PeriodicFunction(UnivariateFunction f, double x0, double x1,
			double rotation) {
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

	public void rotate(double x) {
		rotation += x;
	}

	@Override
	public double value(double x) {
		x += rotation;
		double period = x1 - x0;
		while (x < x0) {
			x += period;
		}
		while (x > x1) {
			x -= period;
		}
		return f.value(x);
	}

}
