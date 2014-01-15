package math.filter;

import org.apache.commons.math3.analysis.UnivariateFunction;

import math.filter.AbstractFilter.RingBuffer;

public interface Filter extends UnivariateFunction {
	/**
	 * The value(double) method should make use of this method to obtain the
	 * value to return.
	 * 
	 * @param x
	 * @return
	 */
	public double elementValue(double x, RingBuffer buffer);

	/**
	 * Gets a array of n filtered values, spaced at equal intervals. (More
	 * efficient than sampling this function externally.)
	 * 
	 * @return
	 */
	public double[] sample(int n);
}
