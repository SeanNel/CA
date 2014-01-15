package math.filter;

import java.util.Arrays;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;

/**
 * Select the median value over a number of samples around each position. When
 * values are required near the endpoints, the function is treated as periodic
 * such that the data is wrapped around.
 * 
 * @author Sean
 */
public class MedianFilter extends AbstractFilter {
	protected final int m;

	public MedianFilter(UnivariateFunction f, double x0, double x1) {
		super(f, x0, x1, (x1 - x0) / 10.0, 10, true);
		m = (int) ((double) numSamples / 2.0);
	}

	public MedianFilter(UnivariateFunction f, double x0, double x1,
			double bandwidth, int numSamples, boolean periodic) {
		super(f, x0, x1, bandwidth, numSamples, periodic);
		m = numSamples / 2;
	}

	@Override
	public double value(double x) {
		if (!periodic) {
			if (x - delta < x0) {
				x = x0 + delta;
			} else if (x + delta > x1) {
				x = x1 - delta;
			}
		}

		double[] samples = FunctionUtils.sample(f, x - delta, x + delta,
				numSamples);
		double result = elementValue(x, new RingBuffer(samples));
		return result;
	}

	@Override
	public double elementValue(double x, RingBuffer buffer) {
		double[] samples = buffer.getArray();
		Arrays.sort(samples);
		return samples[m];
	}
}
