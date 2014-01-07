package math.discrete;

import java.util.List;

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

	public static void pow(double[] f, double c) {
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
	 * Differentiates the discrete function.
	 * <p>
	 * f'(x) = [f(x + dx) - f(x)] / dx
	 * 
	 * @param f
	 */
	public static void differentiate(double[] f) {
		double f0 = f[0];

		for (int x = 0; x < f.length - 1; x++) {
			f[x] = f[x + 1] - f[x];
		}
		int x = f.length - 1;
		f[x] = f0 - f[x];
	}

	/**
	 * Differentiates the specified number of times.
	 * 
	 * @param f
	 * @param n
	 */
	public static void differentiate(double[] f, int n) {
		for (int i = 0; i < n; i++) {
			differentiate(f);
		}
	}

	/**
	 * Gets f'(a)
	 * 
	 * @param f
	 * @param a
	 */
	public static double differentiateAt(double[] f, int a) {
		if (a < f.length - 1) {
			return f[a + 1] - f[a];
		} else {
			return f[0] - f[a];
		}
	}

	/**
	 * Given the polynomial coefficients, plot the y-values against x-values
	 * from 0 to n.
	 * 
	 * @param parameters
	 * @param n
	 * @return
	 */
	public static double[] plotPolynomial(double[] coefficients, int n) {
		double[] f = new double[n];
		int p = coefficients.length;
		for (int x = 0; x < n; x++) {
			double y = 0.0;
			for (int a = 0; a < p; a++) {
				y += coefficients[a] * Math.pow(x, a);
			}
			f[x] = y;
		}
		return f;
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

	protected static void displayPoints(double[] f, List<Integer> points) {
		System.out.println("***");
		for (Integer x : points) {
			System.out.println("x=" + x + ", y=" + f[x]);
		}
	}
}
