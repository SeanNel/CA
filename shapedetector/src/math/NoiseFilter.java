package math;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariatePeriodicInterpolator;

/**
 * Filters noise from a function.
 * 
 * @author Sean
 * 
 */
public class NoiseFilter {
	final static int smoothRadius = 5;

	/**
	 * Filters noise and returns an interpolated polynomial.
	 * 
	 * @param f
	 * @return
	 */
	public static UnivariateFunction filter(DiscreteFunction f) {
		if (f == null) {
			throw new RuntimeException();
		}
		if (f.size() < 3) { // 1
			return f;
		}
		double[] abscissae = f.getAbscissae();
		int n = abscissae.length;
		double x0 = abscissae[0];
		double x1 = abscissae[abscissae.length - 1];

		double[] samples = FunctionUtils.sample(f, x0, x1, n);

		/* Fits to polynomial */
		// int order = n / smoothRadius;
		// g = Polynomial.fit(abscissae, ordinates, order);

		/*
		 * Filters waves with period < 5. Only useful for smooth distributions
		 * such as radial distance (not absolute gradient).
		 */
		// if (n > smoothRadius) {
		// double f0 = 0d;
		// double f1 = n / smoothRadius;
		//
		// samples = FrequencyFilter.filter(samples, f0, f1);
		// }

		return interpolate(samples, x0, x1);
	}

	private static UnivariateFunction interpolate(double[] samples, double x0,
			double x1) {
		double[] abscissae = getAbscissae(x0, x1, samples.length);
		if (samples.length < 3) {
			return new DiscreteFunction(abscissae, samples);
		}

		double bandwidth = 0.1; // LoessInterpolator.DEFAULT_BANDWIDTH;
		int iters = 0; // LoessInterpolator.DEFAULT_ROBUSTNESS_ITERS;
		double accuracy = 1.0; // LoessInterpolator.DEFAULT_ACCURACY;

		UnivariateInterpolator interpolator;
		if ((abscissae.length * bandwidth) < 2) {
			/* Fail-safe option */
			interpolator = new LinearInterpolator();
		} else {
			interpolator = new LoessInterpolator(bandwidth, iters, accuracy);
		}

		/*
		 * We need to have periodic data for the interpolator to work over the
		 * entire domain.
		 */
		interpolator = new UnivariatePeriodicInterpolator(interpolator,
				x1 - x0, 2);

		return interpolator.interpolate(abscissae, samples);

	}

	public static double[] getAbscissae(double x0, double x1, int n) {
		double[] abscissae = new double[n];
		double delta = (x1 - x0) / (double) n;
		double x = x0;
		for (int i = 0; i < n; i++) {
			x += delta;
			abscissae[i] = x;
		}
		return abscissae;
	}

	/**
	 * Kalman Filter. This doesn't work at all at this point but may be worth
	 * looking into.
	 * 
	 * @see http://commons.apache.org/proper/commons-math/userguide/filter.html
	 */
	// public static UnivariateFunction kalmanFilter(UnivariateFunction f,
	// double x0, double x1, int n) {
	// // A = [ 1 ]
	// RealMatrix A = new Array2DRowRealMatrix(new double[] { 1d });
	// // no control input
	// RealMatrix B = null;
	// // H = [ 1 ]
	// RealMatrix H = new Array2DRowRealMatrix(new double[] { 1d });
	// // Q = [ 0 ]
	// RealMatrix Q = new Array2DRowRealMatrix(new double[] { 0 });
	// // R = [ 0 ]
	// RealMatrix R = new Array2DRowRealMatrix(new double[] { 0 });
	//
	// ProcessModel pm = new DefaultProcessModel(A, B, Q, new ArrayRealVector(
	// new double[] { 0 }), null);
	// MeasurementModel mm = new DefaultMeasurementModel(H, R);
	// KalmanFilter filter = new KalmanFilter(pm, mm);
	//
	// double[] samples = FunctionUtils.sample(f, x0, x1, n);
	// RealVector mNoise = new ArrayRealVector(1);
	//
	// for (int i = 0; i < samples.length; i++) {
	// mNoise = mNoise.append(samples[i]);
	// }
	// filter.predict();
	// filter.correct(mNoise);
	//
	// double[] abscissae = new double[samples.length];
	// double[] ordinates = new double[samples.length];
	// double delta = (x1 - x0) / (double) n;
	// double a = 0.0;
	// for (int i = 0; i < samples.length; i++) {
	// a += x0 + delta;
	// abscissae[i] = a;
	// ordinates[i] = mNoise.getEntry(i);
	// }
	//
	// f = new DiscreteFunction(abscissae, ordinates);
	//
	// return f;

	// // iterate 60 steps
	// for (int i = 0; i < 60; i++) {
	// filter.predict(u);
	//
	// // simulate the process
	// // RealVector pNoise = tmpPNoise.mapMultiply(accelNoise
	// // * rand.nextGaussian());
	//
	// // x = A * x + B * u + pNoise
	// // x = A.operate(x).add(B.operate(u)).add(pNoise);
	//
	// // simulate the measurement
	// // mNoise.setEntry(0, measurementNoise * rand.nextGaussian());
	//
	// // z = H * x + m_noise
	// RealVector z = H.operate(x).add(mNoise);
	//
	// filter.correct(z);
	//
	// double position = filter.getStateEstimation()[0];
	// double velocity = filter.getStateEstimation()[1];
	// }

	// }

	/**
	 * Applies a median filter to the function.
	 * 
	 * @param f
	 * @return
	 */
	// public static DiscreteFunctionPeriodic medianFilter(
	// DiscreteFunctionPeriodic f) {
	// double[] fData = f.toArray();
	// Filter.medianFilter(fData, 5);
	// return f = new DiscreteFunctionPeriodic(fData);
	// }

	/**
	 * Applies a mean filter to the function.
	 * 
	 * @param f
	 * @return
	 */
	// public static DiscreteFunctionPeriodic
	// meanFilter(DiscreteFunctionPeriodic f) {
	// double[] fData = f.toArray();
	// Filter.meanFilter(fData, 5);
	// return f = new DiscreteFunctionPeriodic(fData);
	// }

	/**
	 * Filters out noise by regressing the function to a polynomial. The order
	 * used (n/5) is kind of arbitrary but gives a smooth finish. Needs to be
	 * synchronized when running in parallel.
	 * 
	 * @param f
	 * @return
	 */
	// public synchronized static DiscreteFunctionPeriodic regressionSmoothe(
	// DiscreteFunctionPeriodic f) {
	// int n = f.size();
	// double[] coefficients = f.regress(n / 5);
	// return new DiscreteFunctionPeriodic(coefficients, n);

	// PolynomialsUtils.buildPolynomial
	// }

}
