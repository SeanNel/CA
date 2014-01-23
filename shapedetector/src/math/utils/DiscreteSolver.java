package math.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Finds the solutions to a function by approximating it to a step function, and
 * finding adjacent steps that cross the x-axis.
 * 
 * @author Sean
 */
public class DiscreteSolver implements Solver {
	/* Could increase the accuracy magnitude to improve performance. */
	protected final static double DEFAULT_ACCURACY = 1.0; // 1e-6;

	protected final double accuracy;
	protected final boolean periodic;
	protected final List<Double> solutions;

	public DiscreteSolver(final UnivariateFunction f, final double x0,
			final double x1) {
		this(f, x0, x1, DEFAULT_ACCURACY, false);
	}

	public DiscreteSolver(final UnivariateFunction f, final double x0,
			final double x1, final double accuracy, final boolean periodic) {
		if (f == null || x1 <= x0 || accuracy <= 0.0) {
			throw new RuntimeException();
		}
		this.accuracy = accuracy;
		this.periodic = periodic;
		int n = (int) ((x1 - x0) / (double) accuracy);
		if (n <= 0) {
			solutions = new ArrayList<Double>();
			return;
		}
		double[] samples = FunctionUtils.sample(f, x0, x1, n);
		solutions = solve(samples, x0, x1, periodic);
	}

	public static List<Double> solve(final double[] f, final double x0,
			final double x1, final boolean periodic) {
		int n = f.length;
		double m = x1 - x0;
		List<Double> solutions = new ArrayList<Double>();
		if (periodic && (f[n - 1] < 0.0 && f[0] >= 0.0)
				|| (f[n - 1] > 0.0 && f[0] <= 0.0)) {
			solutions.add(0.0);
		}
		int x = 1;
		for (int h = x + 1; h < n; h++) {
			if ((f[x] < 0.0 && f[h] >= 0.0) || (f[x] > 0.0 && f[h] <= 0.0)) {
				double p = ((double) x + x0) / (double) n * m;
				solutions.add(p);
			}
			x++;
		}

		return solutions;
	}

	@Override
	public List<Double> getSolutions() {
		return solutions;
	}
}
