package math;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DiscreteFunction {
	/* TODO: Fourier transform */

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
	public static List<Double> functionDifference(List<Double> f1,
			List<Double> f2) {
		List<Double> f = new ArrayList<Double>(f1.size());
		Iterator<Double> f1Iterator = f1.iterator();
		Iterator<Double> f2Iterator = f2.iterator();
		while (f1Iterator.hasNext()) {
			Double y1 = f1Iterator.next();
			Double y2 = f2Iterator.next();
			f.add(y1 - y2);
		}
		return f;
	}

	/**
	 * Finds x coordinate of absolute minimum in the specified domain.
	 * 
	 * @param discreteFunction
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static int findValley(List<Double> discreteFunction, int x1, int x2) {
		Double minimum = Double.MAX_VALUE;
		int minimumPosition1 = 0;
		int minimumPosition2 = 0;

		Iterator<Double> iterator = discreteFunction.iterator();

		for (int i = 0; i < x1; i++) {
			iterator.next();
		}

		for (int x = x1; iterator.hasNext() && x < x2; x++) {
			Double column = iterator.next();
			if (column < minimum) {
				minimum = column;
				minimumPosition1 = x;
			} else if (column == minimum) {
				minimumPosition2 = x;
			}
		}

		/* Crude estimate of Fourier transform */
		double position = Math.round((minimumPosition2 - minimumPosition1) / 2);
		return (int) position;
	}

	/**
	 * Finds x coordinate of absolute maximum.
	 * 
	 * @param discreteFunction
	 * @return
	 */
	public static int findPeak(List<Double> discreteFunction) {
		Double maximum = 0.0;
		int maximumPosition1 = 0;
		int maximumPosition2 = 0;
		Iterator<Double> iterator = discreteFunction.iterator();

		for (int i = 0; iterator.hasNext(); i++) {
			Double column = iterator.next();
			if (column > maximum) {
				maximum = column;
				maximumPosition1 = i;
			} else if (column == maximum) {
				maximumPosition2 = i;
			}
		}

		/* Crude estimate of Fourier transform */
		double position = Math.round((maximumPosition2 - maximumPosition1) / 2);
		return (int) position;
	}

	/**
	 * Gets the absolute value of the function.
	 * 
	 * @param discreteFunction
	 * @return
	 */
	public static List<Double> absoluteValue(List<Double> discreteFunction) {
		List<Double> absoluteFunction = new ArrayList<Double>(
				discreteFunction.size());
		for (Double y : discreteFunction) {
			absoluteFunction.add(Math.abs(y));
		}
		return absoluteFunction;

	}

	/**
	 * Gets the integral over the entire domain.
	 * 
	 * @param discreteFunction
	 * @return
	 */
	public static double integrate(List<Double> discreteFunction) {
		double integral = 0.0;
		for (Double y : discreteFunction) {
			integral += y;
		}
		return integral;
	}

	/**
	 * Integrates over the specified domain.
	 * 
	 * @param discreteFunction
	 * @param x1
	 * @param x2
	 * @return
	 */
	public static double integrate(List<Double> discreteFunction, int x1, int x2) {
		double integral = 0.0;

		Iterator<Double> iterator = discreteFunction.iterator();
		for (int x = 0; iterator.hasNext() && x < x1; x++) {
			iterator.next();
		}

		for (int x = x1; iterator.hasNext() && x < x2; x++) {
			integral += iterator.next();
		}

		return integral;
	}

	/**
	 * Gets histogram rotated by n positions.
	 * 
	 * @param histogram
	 * @param n
	 * @return
	 */
	public static List<Double> rotateHistogram(List<Double> histogram, int n) {
		histogram = new ArrayList<Double>(histogram);
		Double first = histogram.get(0);
		histogram.remove(0);
		histogram.add(first);
		return histogram;
	}

}
