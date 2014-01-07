package math.discrete;

public class Correlation {

	public static double getCorrelation(double[] f, double g[]) {
		return getPearsonCorrelation(f, g);
	}

	public static double getPearsonCorrelation(double[] scores1,
			double[] scores2) {
		double result = 0;
		double sum_sq_x = 0;
		double sum_sq_y = 0;
		double sum_coproduct = 0;
		double mean_x = scores1[0];
		double mean_y = scores2[0];
		for (int i = 2; i < scores1.length + 1; i += 1) {
			double sweep = Double.valueOf(i - 1) / i;
			double delta_x = scores1[i - 1] - mean_x;
			double delta_y = scores2[i - 1] - mean_y;
			sum_sq_x += delta_x * delta_x * sweep;
			sum_sq_y += delta_y * delta_y * sweep;
			sum_coproduct += delta_x * delta_y * sweep;
			mean_x += delta_x / i;
			mean_y += delta_y / i;
		}
		double pop_sd_x = (double) Math.sqrt(sum_sq_x / scores1.length);
		double pop_sd_y = (double) Math.sqrt(sum_sq_y / scores1.length);
		double cov_x_y = sum_coproduct / scores1.length;
		result = cov_x_y / (pop_sd_x * pop_sd_y);
		return result;
	}

	public static double getCorrelationA(double[] f, double g[]) {
		f = f.clone();
		g = g.clone();
		//
		// DiscreteFunction.normalizeTo(f, 1.0);
		// DiscreteFunction.normalizeTo(g, 1.0);

		f = DiscreteFunction.fit(f, 100);
		g = DiscreteFunction.fit(g, 100);

		double h[] = DiscreteFunction.add(f, g);
		double max = Stats.maximum(h);

		h = DiscreteFunction.difference(f, g);
		DiscreteFunction.absoluteValue(h);

		// dataset.removeAllSeries();
		// dataset.addSeries(distributionChart.getSeries(h, "|f(x)-g(x)|"));
		// distributionChart.setVisible(true);

		return 1.0 - (DiscreteFunction.integrate(h) / h.length / max);
	}

	/* Assumes that the functions have already been normalized as necessary. */
	public static double[] crossCorrelation(double[] f1, double[] f2) {
		int n = f1.length;
		double[] f = new double[n];

		for (int i = 0; i < n; i++) {
			double integral = 0.0;
			int x = i;
			for (int j = 0; j < n; j++) {
				if (x + 1 >= n) {
					x = 0;
				}
				integral += f1[j] * f2[x++];
			}
			f[i] = integral;
		}
		return f;
	}

	/**
	 * Gets the correlation of the functions. TODO: Replace the simple Fourier
	 * transform function with an FFT for improved performance. (O(NlogN)
	 * instead of O(N^2))
	 * 
	 * @param f
	 * @return
	 */

	/* TODO */
	public static double[] crossCrrelationFFT(double[] f1, double[] f2) {
		/* FFT method needs additional normalization */
		int n = f1.length;
		double i[] = new double[n];
		double f1Imag[] = new double[n];
		double f1Real[] = new double[n];
		double f2Imag[] = new double[n];
		double f2Real[] = new double[n];

		Filter.dft(f1, i, f1Real, f1Imag, 0, n);
		Filter.dft(f2, i, f2Real, f2Imag, 0, n);

		/* Gets complex conjugate */
		for (int x = 0; x < n; x++) {
			f1Imag[x] *= -1.0;
		}

		f2 = DiscreteFunction.pointwiseProduct(f1Real, f1Imag, f2Real, f2Imag);
		Filter.dftInverse(f2, i, f1, f1Imag, 0, n);

		return f1;
	}

	/* TODO */
	/**
	 * Gets the autocorrelation of the function. TODO: Replace the simple
	 * Fourier transform function with an FFT for improved performance.
	 * (O(NlogN) instead of O(N^2))
	 * 
	 * @param f
	 * @return
	 */
	public static double[] autoCorrelationFFT(double[] f) {
		int n = f.length;
		double i[] = new double[n];
		double imag[] = new double[n];
		double real[] = new double[n];

		Filter.dft(f, i, real, imag, 0, n);
		f = DiscreteFunction.pointwiseProduct(real, imag, real, imag);
		Filter.dftInverse(f, i, real, imag, 0, n);

		return real;
	}
}
