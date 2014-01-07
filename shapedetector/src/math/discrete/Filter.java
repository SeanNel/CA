package math.discrete;

import java.util.Arrays;

public class Filter {
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

	public static double getMean(double[] f) {
		double mean = 0.0;
		for (int i = 0; i < f.length; i++) {
			mean += f[i];
		}

		return mean / (double) f.length;
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
}
