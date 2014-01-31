package tests;

import exceptions.NullParameterException;
import graphics.LineChartFrame;
import math.DiscreteFunction;
import math.functions.Differential;
import math.functions.PeriodicFunction;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class DifferentiationTest {

	public static void main(String[] args) {
		// test_01();
		test_02();
	}

	/**
	 * Test_01
	 */
	public static void test_01() {
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
		LineChartFrame.frame.getChart().getXYPlot().setRenderer(renderer);

		double x0 = 0d;
		double x1 = 1d;

		/* Creates a sin function */
		double a = 2d * (x1 - x0) * (Math.PI * 2d);
		int n = 60;
		double[] abscissae = new double[n];
		double[] ordinates = new double[n];
		loadSin(abscissae, ordinates, x0, x1, a);

		UnivariateFunction f = new DiscreteFunction(abscissae, ordinates);
		UnivariateFunction df = null;
		try {
			df = new Differential((DiscreteFunction) f);
		} catch (NullParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		UnivariateFunction dfZ = new Differential(f, a / (double) n / 25d);

		// f = new PeriodicFunction(f, x0, x1);
		// df = new PeriodicFunction(df, x0, x1);
		// dfZ = new PeriodicFunction(dfZ, x0, x1);

		/* Displays f(x) and f'(x) */
		// LineChartFrame.displayData(x0, x1, n, f, df, dfZ);

		/* Displays f(x) and f'(x) over an extended domain */
		LineChartFrame.displayData(x0 - 1d, x1 + 1d, n, f, df);

		/*
		 * df wins over dfZ, for the case of differentiating an uninterpolated
		 * DiscreteFunction.
		 */
	}

	/**
	 * Test_02
	 */
	public static void test_02() {
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
		LineChartFrame.frame.getChart().getXYPlot().setRenderer(renderer);

		double x0 = 0d;
		double x1 = 1d;

		/* Creates a sin function */
		double a = 2d * (x1 - x0) * (Math.PI * 2d);
		int n = 60;
		double[] abscissae = new double[n];
		double[] ordinates = new double[n];
		loadSin(abscissae, ordinates, x0, x1, a);

		UnivariateInterpolator interpolator = new LinearInterpolator();
		UnivariateFunction f = interpolator.interpolate(abscissae, ordinates);

		UnivariateFunction df = new Differential(f, a / (double) n / 25d);

		// f = new PeriodicFunction(f, x0, x1);
		// df = new PeriodicFunction(df, x0, x1);

		UnivariateFunction Df = new DiscreteFunction(abscissae, ordinates);
		UnivariateFunction Ddf = null;
		try {
			Ddf = new Differential((DiscreteFunction) Df);
		} catch (NullParameterException e) {
			e.printStackTrace();
		}

		/* Displays f(x) and f'(x) */
		 LineChartFrame.displayData(x0, x1 - 0.1, n, f, df, Df, Ddf);

		/* Displays f(x) and f'(x) over an extended domain */
//		LineChartFrame.displayData(x0 - 1d, x1 + 1d, n, f, df);

		/*
		 * df wins over dfZ, for the case of differentiating an uninterpolated
		 * DiscreteFunction.
		 */

	}

	public static void test_03() {

	}

	// private static void loadPolynomial(double[] abscissae, double[]
	// ordinates,
	// final double x0, final double x1) {
	// int n = abscissae.length;
	// double step = (x1 - x0) / (double) n;
	//
	// double a0 = 3d;
	// double a1 = 2d;
	// double a2 = 0.2d;
	//
	// for (int i = 0; i < n; i++) {
	// double x = (double) i * step;
	// abscissae[i] = x0 + x;
	// ordinates[i] = a0 + a1 * x + a2 * x * x;
	// }
	// }
	private static void loadSin(double[] abscissae, double[] ordinates,
			final double x0, final double x1, final double a) {
		int n = abscissae.length;
		double step = (x1 - x0) / (double) n;

		for (int i = 0; i < n; i++) {
			double x = x0 + (double) i * step;
			abscissae[i] = x;
			ordinates[i] = Math.sin(a * x);
		}
	}
}
