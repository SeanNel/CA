package vectors;

import java.util.Vector;

public class GenericVector extends Vector<Double> {
	private static final long serialVersionUID = 1L;

	public double distance(GenericVector vector) {
		// Compute Euclidian distance between vectors:
		double sum = 0.0;
		for (int i = 0; i < size(); i++) {
			double x = get(i) - vector.get(i);
			sum += x * x;
		}
		return Math.sqrt(sum);
	}
	
//	public GenericVector crossProduct(GenericVector b) {
//		
//	}
}
