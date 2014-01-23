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

	public MedianFilter(final UnivariateFunction f, final double x0,
			final double x1) {
		super(f, x0, x1, (x1 - x0) / 10.0, 10, true);
		m = (int) ((double) numSamples / 2.0);
	}

	public MedianFilter(final UnivariateFunction f, final double x0,
			final double x1, final double bandwidth, final int numSamples,
			final boolean periodic) {
		super(f, x0, x1, bandwidth, numSamples, periodic);
		m = numSamples / 2;
	}

	@Override
	public double value(final double x) {
		double h = x;
		if (!periodic) {
			if (h - delta < x0) {
				h = x0 + delta;
			} else if (x + delta > x1) {
				h = x1 - delta;
			}
		}

		double[] samples = FunctionUtils.sample(f, h - delta, h + delta,
				numSamples);
		double result = elementValue(x, new RingBuffer(samples));
		return result;
	}

	@Override
	public double elementValue(final double x, final RingBuffer buffer) {
		double[] samples = buffer.getArray();
		Arrays.sort(samples);
		return samples[m];
	}
}
