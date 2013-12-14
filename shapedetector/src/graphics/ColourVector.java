package graphics;

import java.util.Vector;

public class ColourVector extends Vector<Float> {
	// This uses the Vector class instead of an array, because I
	// started by working with general vectors before reducing the code to this.
	// In principle, working with a simple array should reduce memory overhead.
	// On the other hand, using any collection of Floats instead of the simple
	// type float is desirable, for the same reason.
	// I chose to use floats because the Color.getColorComponents() method
	// returns a float array, so it makes sense to work in the same format. In
	// addition the integer component is never greater than 255, and floats give
	// greater precision when computing the square root to get distance.

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
