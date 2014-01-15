package math.filter;

import math.functions.Periodic;
import math.functions.PeriodicFunction;

import org.apache.commons.math3.analysis.FunctionUtils;
import org.apache.commons.math3.analysis.UnivariateFunction;

public abstract class AbstractFilter implements Filter {
	protected final UnivariateFunction f;
	protected final double x0;
	protected final double x1;
	protected final int numSamples;
	protected final double bandwidth;
	protected final double delta;
	protected final boolean periodic;

	protected class RingBuffer {
		private int addIndex = 0;
		private int getIndex = 0;
		private double[] buffer;

		public RingBuffer(double[] buffer) {
			this.buffer = buffer;
		}

		public RingBuffer(int n) {
			buffer = new double[n];
		}

		public void add(double x) {
			// if (addIndex >= getIndex) {
			// throw new RuntimeException("Buffer underrun");
			// }
			buffer[addIndex++] = x;
			if (addIndex >= buffer.length) {
				addIndex = 0;
			}
		}

		public double get() {
			// if (getIndex >= addIndex) {
			// throw new RuntimeException("Buffer overrun");
			// }
			double x = buffer[getIndex++];
			if (getIndex > buffer.length) {
				getIndex = 0;
			}
			return x;
		}

		public double[] getArray() {
			return buffer;
		}
	}

	/**
	 * 
	 * @param f
	 * @param numSamples
	 *            The number of samples to take when filtering each element.
	 * @param bandwidth
	 *            Fraction of the domain to apply filter each element over.
	 */
	public AbstractFilter(UnivariateFunction f, double x0, double x1,
			double bandwidth, int numSamples, boolean periodic) {
		if (x0 >= x1 + bandwidth) {
			throw new RuntimeException("Bandwidth is too large for the domain.");
		}
		
		this.x0 = x0;
		this.x1 = x1;
		this.bandwidth = bandwidth;
		this.numSamples = numSamples;
		if (f instanceof Periodic) {
			this.periodic = true;
			this.f = f;
		} else if (periodic) {
			this.periodic = true;
			this.f = new PeriodicFunction(f, x0, x1);
		} else {
			this.periodic = false;
			this.f = f;
		}
		delta = bandwidth / 2.0;
	}

	@Override
	public double[] sample(int n) {
		if (!periodic) {
			throw new RuntimeException();
		}
		double[] values = FunctionUtils.sample(f, x0, x1, n);
		RingBuffer buffer = new RingBuffer(numSamples);
		int h = (numSamples / 2);
		for (int i = 0; i < h; i++) {
			buffer.add(values[i + n - h]);
		}
		for (int i = 0; i < h; i++) {
			buffer.add(values[i]);
		}

		double[] y = new double[n];
		for (int i = 0; i < values.length; i++) {
			int index = i + h;
			if (index >= values.length) {
				index = 0;
			}
			buffer.add(values[index]);

			y[i] = elementValue(values[i], buffer);
		}
		return y;

	}

}
