package math.filter;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.stat.StatUtils;

public class MeanFilter extends AbstractFilter {

	public MeanFilter(UnivariateFunction f, double x0, double x1,
			double bandwidth, int numSamples, boolean periodic) {
		super(f, x0, x1, bandwidth, numSamples, periodic);
	}

	@Override
	public double value(final double x) {
		double h = x;
		if (!periodic) {
			if (x - delta < x0) {
				h = x0 + delta;
			} else if (x + delta > x1) {
				h = x1 - delta;
			}
		}

		double[] samples = FunctionUtils.sample(f, h - delta, h + delta,
				numSamples);
		return elementValue(x, new RingBuffer(samples));
	}

	@Override
	public double elementValue(final double x, final RingBuffer buffer) {
		double[] samples = buffer.getArray();
		return StatUtils.mean(samples);
	}

}
