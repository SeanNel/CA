package vectors;

import java.util.Vector;

public class ColourVector extends Vector<Float> {
	// I chose to use floats because the integer component is never greater than
	// 255, and floats give greater precision when computing the square root to
	// get distance.

	private static final long serialVersionUID = 1L;

	public ColourVector() {
	}

	public ColourVector(float[] components) {
		for (int i = 0; i < components.length; i++) {
			add(components[i]);
		}
	}

	public float distance(ColourVector v2) {
		// Compute Euclidian distance between vectors:
		double sum = 0.0;
		for (int i = 0; i < size(); i++) {
			double x = get(i) - v2.get(i);
			sum += x * x;
		}
		return (float) Math.sqrt(sum);
	}
}
