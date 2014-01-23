package math.utils;

import java.util.Comparator;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;
import org.apache.commons.math3.analysis.differentiation.UnivariateDifferentiableFunction;

/**
 * Sorts a list of x-coordinates according to specified criteria.
 * 
 * @author Sean
 */
public class CriticalPointComparator implements Comparator<Double> {
	public final static int INCREASING_X = 0;
	public final static int MAXIMUM_Y = 1;
	public final static int MINIMUM_Y = 2;
	public final static int SECOND_DERIVATIVE = 3;
	protected final UnivariateDifferentiableFunction f;
	protected final int comparisonType;

	public CriticalPointComparator(final UnivariateDifferentiableFunction f,
			int comparisonType) {
		if (f == null) {
			throw new RuntimeException();
		}
		this.f = f;
		this.comparisonType = comparisonType;
	}

	public CriticalPointComparator(final UnivariateDifferentiableFunction f) {
		this(f, INCREASING_X);
	}

	@Override
	public int compare(final Double x1, final Double x2) {
		switch (comparisonType) {
		case MAXIMUM_Y:
			return compareMaximum(x1, x2);
		case MINIMUM_Y:
			return compareMinimum(x1, x2);
		case SECOND_DERIVATIVE:
			return compareDerivative(x1, x2, 2);
		case INCREASING_X:
		default:
			return compareIncreasing(x1, x2);
		}
	}

	public int compareIncreasing(final double x1, final double x2) {
		if (x1 < x2) {
			return -1;
		} else if (x1 > x2) {
			return 1;
		}
		return 0;
	}

	public int compareMaximum(final double x1,final  double x2) {
		double y1 = f.value(x1);
		double y2 = f.value(x2);

		if (y1 > y2) {
			return -1;
		} else if (y1 < y2) {
			return 1;
		}
		return 0;
	}

	public int compareMinimum(double x1, double x2) {
		double y1 = f.value(x1);
		double y2 = f.value(x2);

		if (y1 < y2) {
			return -1;
		} else if (y1 > y2) {
			return 1;
		}
		return 0;
	}

	public int compareDerivative(double x1, double x2, int order) {
		DerivativeStructure f1 = new DerivativeStructure(1, order, 0, x1);
		DerivativeStructure f2 = new DerivativeStructure(1, order, 0, x2);
		double y1 = f1.getPartialDerivative(order);
		double y2 = f2.getPartialDerivative(order);
		if (y1 == y2) {
			return 0;
		} else if (y1 < y2) {
			return -1;
		} else {
			return 1;
		}
	}

}