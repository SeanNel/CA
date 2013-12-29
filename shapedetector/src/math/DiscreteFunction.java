package math;

public class DiscreteFunction {

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
	public static void smoothe(double[] f, int delta) {
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
	 * Gets the median (average) of a number of data points.
	 * 
	 * @param f
	 *            Array of data points.
	 * @param x1
	 *            Start offset.
	 * @param delta
	 *            Number of samples to average over.
	 * @return
	 */
	public static double getMedian(double[] f, int x1, int delta) {
		double median = 0.0;

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
			median += f[x];
		}

		return median / (double) delta;
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
	 * Gets the autocorrelation of the function. TODO: Replace the simple
	 * Fourier transform function with an FFT for improved performance.
	 * (O(NlogN) instead of O(N^2))
	 * 
	 * @param f
	 * @return
	 */
	public static double[] autoCorrelation(double[] f) {
		double i[] = new double[f.length];
		double imag[] = new double[f.length];
		double real[] = new double[f.length];

		dft(f, i, real, imag, 0, f.length);
		f = getModulusSquared(real, imag);
		dftInverse(f, i, real, imag, 0, f.length);
		f = real;

		return f;
	}

	/**
	 * Filters the input signal for frequencies between min and max values.
	 * 
	 * @param f
	 * @param min
	 * @param max
	 * @return
	 */
	public static double[] freqBand(double[] f, int min, int max) {
		double i[] = new double[f.length];
		double imag[] = new double[f.length];
		double real[] = new double[f.length];

		dftInverse(f, i, real, imag, min, max);
		dft(real, imag, f, i, 0, f.length);

		return f;
	}

	/**
	 * Gets a a sign wave (for debugging purposes)
	 * 
	 * @param length
	 * @param factor
	 * @return
	 */
	protected static double[] getSin(int length, double factor) {
		double[] f = new double[length];
		for (int x = 0; x < f.length; x++) {
			f[x] = Math.sin(x * factor);
		}
		return f;
	}

	/**
	 * Gets the modulus squared of a complex function.
	 * 
	 * @param real
	 *            An array of real components
	 * @param imag
	 * @return
	 */
	protected static double[] getModulusSquared(double[] real, double[] imag) {
		double[] f = new double[real.length];
		for (int x = 0; x < real.length; x++) {
			f[x] = real[x] * real[x] + imag[x] * imag[x];
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

	public static double[] times(double[] f, double x) {
		for (int i = 0; i < f.length; i++) {
			f[i] *= x;
		}
		return f;
	}

	public static void add(double[] f, double d) {
		for (int x = 0; x < f.length; x++) {
			f[x] += d;
		}
	}

	public static double[] dotProduct(double[] f1, double[] f2) {
		double[] f = new double[f1.length];
		for (int x = 0; x < f.length; x++) {
			f[x] = f1[x] * f2[x];
		}
		return f;
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
	public static double[] rotate(double[] f, int n) {
		double[] f1 = new double[f.length];

		for (int x = 0; x < n; x++) {
			f1[x] = f[f.length - x - 1];
		}

		for (int x = 0; x < f.length - n; x++) {
			f1[x + n] = f[x];
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
