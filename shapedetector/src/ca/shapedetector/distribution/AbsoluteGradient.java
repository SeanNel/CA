package ca.shapedetector.distribution;

import helpers.Misc;

import java.awt.geom.Point2D;

import math.DiscreteFunction;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariatePeriodicInterpolator;

/**
 * In principle, this method should be able to find vertices of most polygonal
 * shapes, but because the angles are quantized the graph is too irregular to be
 * of use.
 * <p>
 * The results should be similar to taking the differentiation of the
 * RadialDistance graph, and subtracting the angle between the centroid and each
 * point along the outline.
 * 
 * @author Sean
 */
public class AbsoluteGradient extends Distribution {

	protected double getValue(final Point2D o, final Point2D a, final Point2D b) {
		double theta = getGradient(a, b);
		return theta;
	}

	/**
	 * Gets the angle between two points.
	 * 
	 * @param v1
	 * @param v2
	 * @return
	 */
	protected static double getGradient(Point2D v1, Point2D v2) {
		double x = v2.getX() - v1.getX();
		double y = v2.getY() - v1.getY();

		/* Y is negative because it increases from top to bottom. */
		double theta = Misc.getAngle(x, -y);

		if (theta < 0) {
			theta += 2.0 * Math.PI;
		}
		// gradient -= (Math.PI / 2.0);
		return theta;
	}

	/**
	 * Filters noise and returns an interpolated polynomial.
	 * <p>
	 * Applying a mean filter makes the slopes less steep, such that each vertex
	 * creates two critical points. A median filter creates vertices along
	 * straight, though slanted lines.
	 * <p>
	 * So this method is not practical until a way is found to effectively
	 * filter out the noise.
	 * 
	 * @param f
	 * @return
	 */
	public UnivariateFunction filter(final DiscreteFunction f) {
		if (f == null) {
			throw new RuntimeException();
		}
		if (f.size() < 3) {
			return f;
		}
		double[] a = f.getAbscissae();
		double x0 = a[0];
		double x1 = a[a.length - 1];

		double[] samples = FunctionUtils.sample(f, x0, x1, a.length);

		// double bandwidth = 5.0;
		// int numSamples = (int) (bandwidth * 2.0);
		// Filter g = new MeanFilter(f, x0, x1, bandwidth, numSamples, true);
		// double[] samples = g.sample(a.length);

		// bandwidth = 4.0;
		// numSamples = (int) (bandwidth * 2.0);
		// // g = new MedianFilter(g, x0, x1, bandwidth, numSamples, true);
		// Filter g = new MedianFilter(f, x0, x1, bandwidth, numSamples, true);
		// double[] samples = g.sample(a.length);

		// UnivariateFunction h = new DiscreteFunction(samples, x0, x1);
		// graphics.LineChartFrame.displayData(x0, x1, f, h);

		double[] abscissae = DiscreteFunction.getAbscissae(x0, x1,
				samples.length);
		double[] ordinates = samples;
		return interpolate(abscissae, ordinates);
	}

	protected UnivariateFunction interpolate(final double[] abscissae,
			final double[] ordinates) {
		int n = abscissae.length;
		if (n < 3) {
			return new DiscreteFunction(abscissae, ordinates);
		}

		// double bandwidth = LoessInterpolator.DEFAULT_BANDWIDTH;
		// int iters = LoessInterpolator.DEFAULT_ROBUSTNESS_ITERS;
		// double accuracy = LoessInterpolator.DEFAULT_ACCURACY;

		// double bandwidth = 0.05;
		// int iters = 0;
		// double accuracy = 1;

		UnivariateInterpolator interpolator;
		// if ((n * bandwidth) < 2) {
		// /* Fail-safe option */
		// interpolator = new LinearInterpolator();
		// } else {
		// interpolator = new LoessInterpolator(bandwidth, iters, accuracy);
		// }
		interpolator = new LinearInterpolator();

		/*
		 * We need to have periodic data for the interpolator to work over the
		 * entire domain. NB: The first abscissa must > 0 for the periodic
		 * interpolator to work. Then the period is equal to x1, not x1-x0.
		 */
		// double x0 = abscissae[0];
		double x1 = abscissae[n - 1];
		interpolator = new UnivariatePeriodicInterpolator(interpolator, x1, 2);

		return interpolator.interpolate(abscissae, ordinates);
	}
}
