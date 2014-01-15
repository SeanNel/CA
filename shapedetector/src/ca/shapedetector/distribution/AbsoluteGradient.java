package ca.shapedetector.distribution;

import java.awt.geom.Point2D;

import math.DiscreteFunction;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariatePeriodicInterpolator;

/**
 * In principle, this method should be able to find vertices of most polygonal
 * shapes, but because the angles are quantized the graph is too irregular to be
 * of use.
 * 
 * @author Sean
 * 
 */
public class AbsoluteGradient extends Distribution {

	protected double getValue(Point2D o, Point2D a, Point2D b) {
		double theta = getGradient(a, b);
		return theta;
	}

	public static double getAngle(double x, double y) {
		if (x == 0) {
			if (y >= 0) {
				return Math.PI / 2.0;
			} else {
				return 1.5 * Math.PI;
			}
		} else if (y == 0) {
			if (x >= 0) {
				return 0;
			} else {
				return Math.PI;
			}
		} else {
			double angle = Math.atan(Math.abs(y / x));
			if (x < 0 && y > 0) {
				angle = Math.PI - angle;
			} else if (x < 0 && y < 0) {
				angle = Math.PI + angle;
			} else if (x > 0 && y < 0) {
				angle = 2.0 * Math.PI - angle;
			}
			return angle;
		}
	}

	protected static double getGradient(Point2D v1, Point2D v2) {
		/* Y is negative because it increases from top to bottom. */
		double theta1 = getAngle(v1.getX(), -v1.getY());
		double theta2 = getAngle(v2.getX(), -v2.getY());

		double gradient = theta2 - theta1;
		if (gradient < 0) {
			gradient += 2.0 * Math.PI;
		}
		// gradient -= (Math.PI / 2.0);
		return gradient;
	}

	/**
	 * Filters noise and returns an interpolated polynomial.
	 * 
	 * @param f
	 * @return
	 */
	public UnivariateFunction filter(DiscreteFunction f) {
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

		// double bandwidth = 2.0;
		// int numSamples = (int) (bandwidth * 2.0);
		// Filter g = new MeanFilter(f, x0, x1, bandwidth, numSamples, true);
		// double[] samples = g.sample(a.length);

		// bandwidth = 4.0;
		// numSamples = (int) (bandwidth * 2.0);
		// g = new MedianFilter(g, x0, x1, bandwidth, numSamples, true);
		// double[] samples = g.sample(a.lesngth);

		// UnivariateFunction h = new DiscreteFunction(samples, x0, x1);
		// graphics.LineChartFrame.displayData(x0, x1, f, h);

		double[] abscissae = DiscreteFunction.getAbscissae(x0, x1,
				samples.length);
		double[] ordinates = samples;
		return interpolate(abscissae, ordinates);
	}

	protected UnivariateFunction interpolate(double[] abscissae,
			double[] ordinates) {
		int n = abscissae.length;
		if (n < 3) {
			return new DiscreteFunction(abscissae, ordinates);
		}

		// double bandwidth = LoessInterpolator.DEFAULT_BANDWIDTH;
		// int iters = LoessInterpolator.DEFAULT_ROBUSTNESS_ITERS;
		// double accuracy = LoessInterpolator.DEFAULT_ACCURACY;

		/* Gradient method */
		double bandwidth = 0.001;
		int iters = 0;
		double accuracy = 1;

		UnivariateInterpolator interpolator;
		if ((n * bandwidth) < 2) {
			/* Fail-safe option */
			interpolator = new LinearInterpolator();
		} else {
			interpolator = new LoessInterpolator(bandwidth, iters, accuracy);
		}
		// interpolator = new LinearInterpolator();

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
