package math.functions;

import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * TODO
 * */
public class CrossCorrelation implements UnivariateFunction {
	protected UnivariateFunction f1;
	protected UnivariateFunction f2;
	protected double x0;
	protected double x1;
	protected double delta;

	double normalizationFactor;

	public CrossCorrelation(UnivariateFunction f1, UnivariateFunction f2,
			double delta, double x0, double x1) {
		// int theta = f1.phaseDifference(f2);
		// f2.rotate(theta);
		// double similarity = f1.similarity(f2);

		// double m = FastMath.max(maximum(), f.maximum());
		// double correlation = dotProduct(f) / m / (double) n;
		// // double x = Math.E;
		// // correlation = Math.log(correlation * x) / Math.log(x);
		// return correlation;
	}

	@Override
	public double value(double x) {
		return 0.0;
	}

	protected void x() {

		/*
		 * This method doesn't work (always returns 1.0). Cross-correlation does
		 * not seem to be an accurate measure of similarity between functions.
		 */
		/* TODO: fix problem: crossCorrelation always peaks at 0 */
		// DiscreteFunctionPeriodic g = f1.crossCorrelation(f2);
		// double correlation = g.maximum();
		//
		// /* Gets the autocorrelation. */
		// DiscreteFunctionPeriodic h = f1.crossCorrelation(f1);
		// /* The 1st value of an autocorrelation is always the maximum. */
		// double norm = h.value(0);
		//
		// /* Normalizes the correlation. */
		// double similarity = correlation / norm;
	}

	/**
	 * Computes the cross correlation in O(NlogN) time. Unfortunately this does
	 * not seem as accurate as the other method, below.
	 * 
	 * @param f
	 * @return
	 */
	// public DiscreteFunctionPeriodic crossCorrelation(
	// DiscreteFunctionPeriodic f) {
	// int n = f.size();
	//
	// DiscreteFunction f1 = new DiscreteFunction(this);
	// DiscreteFunction f2 = new DiscreteFunction(f);
	//
	// FastFourierTransformer fft = new FastFourierTransformer(
	// DftNormalization.STANDARD); // UNITARY?
	//
	// // /*
	// // * Adds padding (non-zero). FFT requires that the length be a power of
	// // * 2.
	// // */
	// // f1.resize(leastPower(f1.size(), 2), RESIZE_LOOP);
	// // f2.resize(leastPower(f2.size(), 2), RESIZE_LOOP); // CROP
	//
	// /* FFT requires that the length be a power of 2. */
	// f1.resize(leastPower(f1.size(), 2), ResizeMethod.STRETCH);
	// f2.resize(leastPower(f2.size(), 2), ResizeMethod.STRETCH);
	//
	// /* Transforms data to frequency domain. */
	// Complex[] f1c, f2c;
	// f1c = fft.transform(f1.toArray(), TransformType.FORWARD);
	// f2c = fft.transform(f2.toArray(), TransformType.FORWARD);
	//
	// /* Point-wise product, then get norm. */
	// for (int x = 0; x < f1c.length; x++) {
	// f1c[x].multiply(f2c[x]);
	// f1c[x] = new Complex(f1c[x].abs(), 0.0);
	// }
	//
	// /* Transforms back to space domain. */
	// f2c = fft.transform(f1c, TransformType.INVERSE);
	//
	// /* Creates a function containing real data. */
	// DiscreteFunctionPeriodic g = new DiscreteFunctionPeriodic(n);
	// for (int x = 0; x < f2c.length; x++) {
	// g.append(f2c[x].getReal());
	// }
	//
	// g.resize(n, ResizeMethod.STRETCH);
	// // graphics.LineChartFrame.displayData(this, f, g);
	// // graphics.LineChartFrame.displayData(f1, f2, g);
	// return g;
	// }

	/**
	 * Returns a power of 2, at least as large as x.
	 * 
	 * @param x
	 * @param n
	 * @return
	 */
	protected int leastPower(int x, int n) {
		double p = Math.log(x) / Math.log(n);
		if (p != (int) p) {
			x = (int) Math.pow(n, Math.ceil(p));
		}
		return x;
	}

}
