package ca.shapedetector.distribution;

import java.awt.geom.Point2D;

import math.DiscreteFunction;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariatePeriodicInterpolator;

/**
 * This method suffers from limitations, but because the graph is smooth and
 * continuous, it works well for finding the vertices of rectangles that are not
 * too thin. It is not able to detect vertices that that are at an obtuse angle
 * to the centroid, so some parallelpipeds won't be detected.
 * 
 * @author Sean
 * 
 */
public class RadialDistance extends Distribution {

	protected double getValue(Point2D o, Point2D a, Point2D b) {
		return o.distance(b);
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

		// double bandwidth = 0.01; //0.05;
		// int iters = 0;
		// double accuracy = 2.0;
		
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
