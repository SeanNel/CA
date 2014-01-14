package math;

import org.apache.commons.math3.analysis.differentiation.DerivativeStructure;

public class ComparableDerivativeStructure implements Comparable<DerivativeStructure> {
	public final DerivativeStructure t;
	public final int order;

	public ComparableDerivativeStructure(DerivativeStructure t, int order) {
		this.t = t;
		this.order = order;
	}

	@Override
	public int compareTo(DerivativeStructure arg0) {
		double y0 = t.getPartialDerivative(order);
		double y1 = arg0.getPartialDerivative(order);
		if (y0 == y1) {
			return 0;
		} else if (y0 < y1) {
			return -1;
		} else {
			return 1;
		}
	}
}