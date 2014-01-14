package math.utils;

import java.util.Comparator;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class CriticalPointComparator implements Comparator<Double> {
	public final static int MAXIMUM = 0;
	public final static int MINIMUM = 1;
	public final static int INCREASING = 2;
	protected final UnivariateFunction f;
	protected final int mode;

	public CriticalPointComparator(UnivariateFunction f, int mode) {
		this.f = f;
		this.mode = mode;
	}

	public CriticalPointComparator(UnivariateFunction f) {
		this.f = f;
		this.mode = MAXIMUM;
	}

	@Override
	public int compare(Double x1, Double x2) {
		switch (mode) {
		case MAXIMUM:
			return compareMaximum(x1, x2);
		case MINIMUM:
			return compareMinimum(x1, x2);
		case INCREASING:
		default:
			return compareIncreasing(x1, x2);
		}
	}

	public int compareIncreasing(Double x1, Double x2) {
		if (x1 < x2) {
			return -1;
		} else if (x1 > x2) {
			return 1;
		}
		return 0;
	}

	public int compareMaximum(Double x1, Double x2) {
		double y1 = f.value(x1);
		double y2 = f.value(x2);

		if (y1 > y2) {
			return -1;
		} else if (y1 < y2) {
			return 1;
		}
		return 0;
	}

	public int compareMinimum(Double x1, Double x2) {
		double y1 = f.value(x1);
		double y2 = f.value(x2);

		if (y1 < y2) {
			return -1;
		} else if (y1 > y2) {
			return 1;
		}
		return 0;
	}
}