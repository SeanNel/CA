package math;

import graphics.LineChart;

import java.util.Arrays;

import org.jfree.data.xy.XYIntervalSeriesCollection;


public class DiscreteFunction {
	/* Keeps a chart handy to display distribution data. */
	protected static final XYIntervalSeriesCollection dataset = new XYIntervalSeriesCollection();
	public static final LineChart distributionChart = new LineChart(dataset);

	public static void normalize(double[] f) {
		double norm = Math.sqrt(dotProduct(f, f));
		times(f, 1.0 / norm);
	}

	public static void normalizeTo(double[] f, double c) {
		double norm = c * (double) f.length;
		times(f, 1.0 / norm);
	}

	public static double getCorrelation(double[] f, double g[]) {
		// f = f.clone();
		// g = g.clone();
		//
		// DiscreteFunction.normalizeTo(f, 1.0);
		// DiscreteFunction.normalizeTo(g, 1.0);

		double h[] = DiscreteFunction.difference(f, g);
		DiscreteFunction.absoluteValue(h);

		// dataset.removeAllSeries();
		// dataset.addSeries(distributionChart.getSeries(h, "|f(x)-g(x)|"));
//		distributionChart.setVisible(true);

		return 1.0 - (DiscreteFunction.integrate(h) / h.length);
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

		dft(f1, i, f1Real, f1Imag, 0, n);
		dft(f2, i, f2Real, f2Imag, 0, n);

		/* Gets complex conjugate */
		for (int x = 0; x < n; x++) {
			f1Imag[x] *= -1.0;
		}

		f2 = pointwiseProduct(f1Real, f1Imag, f2Real, f2Imag);
		dftInverse(f2, i, f1, f1Imag, 0, n);

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

		dft(f, i, real, imag, 0, n);
		f = pointwiseProduct(real, imag, real, imag);
		dftInverse(f, i, real, imag, 0, n);

		return real;
	}

	/**
	 * Filters the input signal for waves with period between min and max
	 * values.
	 * 
	 * @param f
	 * @param min
	 * @param max
	 * @return
	 */
	public static double[] bandPass(double[] f, int min, int max) {
		double i[] = new double[f.length];
		double imag[] = new double[f.length];
		double real[] = new double[f.length];

		dftInverse(f, i, real, imag, min, max);
		dft(real, imag, f, i, 0, f.length);

		return f;
	}

	/**
	 * Stretches or compresses the function to the specified length.
	 * 
	 * @param f1
	 * @param length
	 */
	public static double[] fit(double[] f1, int length) {
		double[] f = new double[length];

		for (int x = 0; x < length; x++) {
			double a = (double) f1.length / (double) length * (double) x;
			f[x] = f1[(int) Math.floor(a)];
		}
		return f;
	}

	public static double[] crop(double[] f1, int length) {
		double[] f = new double[length];

		for (int x = 0; x < length; x++) {
			f[x] = f1[x];
		}
		return f;
	}

	/**
	 * Smoothes out the function over the number of data points specified.
	 * 
	 * @param f1
	 * @param delta
	 */
	public static void meanFilter(double[] f, int delta) {
		if (f.length < delta) {
			return;
		}

		double[] f1 = f.clone();
		int delta1 = (int) Math.floor(delta / 2.0);

		for (int x = 0; x < f.length; x++) {
			f[x] = getMean(f1, x - delta1, delta);
		}
	}

	/**
	 * Gets the mean (average) of a number of data points.
	 * 
	 * @param f
	 *            Array of data points.
	 * @param x1
	 *            Start offset.
	 * @param delta
	 *            Number of samples to average over.
	 * @return
	 */
	public static double getMean(double[] f, int x1, int delta) {
		double mean = 0.0;

		if (x1 < 0) {
			x1 = f.length + x1;
		} else if (x1 >= f.length) {
			x1 = x1 - f.length;
		}

		for (int i = 0; i < delta; i++) {
			int x = x1 + i;
			if (x >= f.length) {
				x = x - f.length;
			}
			mean += f[x];
		}

		return mean / (double) delta;
	}

	/**
	 * Smoothes out the function over the number of data points specified.
	 * 
	 * @param f1
	 * @param delta
	 */
	public static void medianFilter(double[] f, int delta) {
		if (f.length < delta) {
			return;
		}

		double[] f1 = f.clone();
		int delta1 = (int) Math.floor(delta / 2.0);

		for (int x = 0; x < f.length; x++) {
			f[x] = getMedian(f1, x - delta1, delta);
		}
	}

	/**
	 * Gets the median of a number of data points.
	 * 
	 * @param f
	 *            Array of data points.
	 * @param x1
	 *            Start offset.
	 * @param delta
	 *            Number of samples to average over.
	 * @return
	 */
	public static double getMedian(double[] f, int x, int delta) {
		if (x < 0) {
			x += f.length;
		}

		double[] f1 = new double[delta];
		for (int i = 0; i < delta; i++) {
			if (x >= f.length) {
				x = x - f.length;
			}
			f1[i] = f[x++];
		}
		Arrays.sort(f1);
		int m = (int) Math.round((double) delta / 2.0);
		return f1[m];
	}

	/**
	 * Gets the difference between two discrete functions (assumes both have the
	 * same domain).
	 * <p>
	 * f(x) = f1(x) - f2(x)
	 * 
	 * @param f1
	 * @param f2
	 * @return
	 */
	public static double[] difference(double[] f1, double[] f2) {
		double[] f = new double[f1.length];

		for (int x = 0; x < f1.length; x++) {
			f[x] = f1[x] - f2[x];
		}
		return f;
	}

	/**
	 * Finds x coordinate of the absolute minimum.
	 * 
	 * @param f
	 * @return
	 */
	public static int findValley(double[] f) {
		return findValley(f, 0, f.length);
	}

	/**
	 * Finds x coordinate of the absolute minimum in the specified domain.
	 * 
	 * @param f
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static int findValley(double[] f, int x1, int x2) {
		double minimum = Double.MAX_VALUE;
		int minimumPosition = 0;

		for (int x = x1; x < x2; x++) {
			double y = f[x];
			if (y < minimum) {
				minimum = y;
				minimumPosition = x;
			}
		}

		return minimumPosition;
	}

	/**
	 * Finds x coordinate of the absolute maximum.
	 * 
	 * @param f
	 * @return
	 */
	public static int findPeak(double[] f) {
		return findPeak(f, 0, f.length);
	}

	/**
	 * Finds x coordinate of the absolute maximum in the specified domain.
	 * 
	 * @param f
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static int findPeak(double[] f, int x1, int x2) {
		double maximum = Double.MIN_VALUE;
		int maximumPosition = 0;

		for (int x = x1; x < x2; x++) {
			double y = f[x];
			if (y > maximum) {
				maximum = y;
				maximumPosition = x;
			}
		}

		return maximumPosition;
	}

	/**
	 * Gets the absolute value of the function.
	 * 
	 * @param f
	 */
	public static void absoluteValue(double[] f) {
		for (int x = 0; x < f.length; x++) {
			f[x] = Math.abs(f[x]);
		}

	}

	/**
	 * Gets the integral over the entire domain.
	 * 
	 * @param f
	 * @return
	 */
	public static double integrate(double[] f) {
		double integral = 0.0;
		for (double y : f) {
			integral += y;
		}
		return integral;
	}

	/**
	 * Integrates over the specified domain.
	 * 
	 * @param f
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static double integrate(double[] f, int x1, int x2) {
		double integral = 0.0;

		for (int x = x1; x < x2; x++) {
			integral += f[x];
		}

		return integral;
	}

	/**
	 * Gets a sign wave with the given time period.
	 * 
	 * @param length
	 * @param factor
	 * @return
	 */
	public static double[] getSin(int length, double period, double offset) {
		double[] f = new double[length];
		for (int x = 0; x < f.length; x++) {
			f[x] = Math.sin((x - offset) * Math.PI * 2.0 / period);
		}
		return f;
	}

	/**
	 * Simple implementation of the Fourier transform. That is, it converts a
	 * function of time to a function of frequency.
	 * 
	 * @see http 
	 *      ://nayuki.eigenstate.org/page/how-to-implement-the-discrete-fourier
	 *      -transform
	 * 
	 * @param inreal
	 * @param inimag
	 * @param outreal
	 * @param outimag
	 */
	public static void dft(double[] inreal, double[] inimag, double[] outreal,
			double[] outimag, int kMin, int kMax) {
		int n = inreal.length;
		for (int k = kMin; k < n && k < kMax; k++) {
			double sumreal = 0;
			double sumimag = 0;
			for (int t = 0; t < n; t++) { // For each input element
				sumreal += inreal[t] * Math.cos(2.0 * Math.PI * t * k / n)
						+ inimag[t] * Math.sin(2.0 * Math.PI * t * k / n);
				sumimag += -inreal[t] * Math.sin(2.0 * Math.PI * t * k / n)
						+ inimag[t] * Math.cos(2.0 * Math.PI * t * k / n);
			}
			outreal[k] = sumreal;
			outimag[k] = sumimag;
		}
	}

	/**
	 * Simple implementation of the inverse Fourier transform. That is, it
	 * converts a function of frequency to a function of time.
	 * 
	 * @param inreal
	 * @param inimag
	 * @param outreal
	 * @param outimag
	 * @param kMin
	 * @param kMax
	 */
	public static void dftInverse(double[] inreal, double[] inimag,
			double[] outreal, double[] outimag, int kMin, int kMax) {
		int n = inreal.length;
		for (int k = kMin; k < n && k < kMax; k++) {
			double sumreal = 0;
			double sumimag = 0;
			for (int t = 0; t < n; t++) { // For each input element
				sumreal += inreal[t] * Math.cos(2.0 * Math.PI * t * k / n)
						+ inimag[t] * Math.sin(2.0 * Math.PI * t * k / n);
				sumimag += inreal[t] * Math.sin(2.0 * Math.PI * t * k / n)
						+ inimag[t] * Math.cos(2.0 * Math.PI * t * k / n);
			}
			outreal[k] = sumreal;
			outimag[k] = sumimag;
		}
	}

	public static void times(double[] f, double c) {
		for (int i = 0; i < f.length; i++) {
			f[i] *= c;
		}
	}

	public static void add(double[] f, double c) {
		for (int x = 0; x < f.length; x++) {
			f[x] += c;
		}
	}

	public static double[] add(double[] f1, double f2[]) {
		double f[] = new double[f1.length];
		for (int x = 0; x < f.length; x++) {
			f[x] = f1[x] + f2[x];
		}
		return f;
	}

	private static void pow(double[] f, double c) {
		for (int x = 0; x < f.length; x++) {
			f[x] = Math.pow(f[x], c);
		}
	}

	public static double[] pointwiseProduct(double[] f1, double[] f2) {
		double[] f = new double[f1.length];
		for (int x = 0; x < f.length; x++) {
			f[x] = f1[x] * f2[x];
		}
		return f;
	}

	public static double[] pointwiseProduct(double[] f1Real, double[] f1Imag,
			double[] f2Real, double[] f2Imag) {
		double[] f = new double[f1Real.length];
		for (int x = 0; x < f.length; x++) {
			f[x] = f1Real[x] * f2Real[x] + f1Imag[x] * f2Imag[x];
		}
		return f;
	}

	public static double dotProduct(double[] f1, double[] f2) {
		double f[] = pointwiseProduct(f1, f2);
		return integrate(f);
	}

	/**
	 * Loops the data a specified number of times.
	 * 
	 * @param f
	 * @param factor
	 * @return
	 */
	public static double[] loop(double[] f, double factor) {
		int n = (int) ((double) f.length * factor);
		double[] f1 = new double[n];

		int x = 0;
		for (int i = 0; i < n; i++) {
			x++;
			if (x >= f.length) {
				x = 0;
			}
			f1[i] = f[x];
		}
		return f1;
	}

	/**
	 * Takes a function of periodic data, and wrapping it around the ends,
	 * shifts it the specified number of steps.
	 * 
	 * @param f
	 * @param n
	 * @return
	 */
	public static double[] rotate(double[] f, int delta) {
		int n = f.length;
		double[] f1 = new double[n];

		while (delta >= n) {
			delta -= n;
		}
		while (delta < 0) {
			delta += n;
		}

		for (int x = 0; x < delta; x++) {
			f1[x] = f[n - x - 1];
		}

		for (int x = 0; x < n - delta; x++) {
			f1[x + delta] = f[x];
		}
		return f1;
	}

	/**
	 * Gets the mean value (average) of the function.
	 * 
	 * @param f
	 * @return
	 */
	public static double average(double[] f) {
		return integrate(f) / f.length;
	}

	/**
	 * Gets the maximum value of the function.
	 * 
	 * @param f
	 * @return
	 */
	public static double maximum(double[] f) {
		double maximum = 0.0;

		for (int x = 0; x < f.length; x++) {
			if (f[x] > maximum) {
				maximum = f[x];
			}
		}
		return maximum;
	}

	/**
	 * Gets the minimum value of the function.
	 * 
	 * @param f
	 * @return
	 */
	public static double minimum(double[] f) {
		double minimum = Double.MAX_VALUE;

		for (int x = 0; x < f.length; x++) {
			if (f[x] < minimum) {
				minimum = f[x];
			}
		}
		return minimum;
	}
}
