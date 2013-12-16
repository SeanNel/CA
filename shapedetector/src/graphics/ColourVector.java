package graphics;

/**
 * Used for computing the difference between colours.
 * <p>
 * The Color.getColorComponents() method returns a float array, so it makes
 * sense to work in the same format. In addition the integer component is never
 * greater than 255, and floats give greater precision when computing the square
 * root to get distance.
 * 
 * @author Sean
 */
public class ColourVector {
	float[] components;

	/**
	 * Constructor.
	 * 
	 * @param components
	 *            Array of colour components.
	 */
	public ColourVector(float[] components) {
		this.components = components;
	}

	/**
	 * Computes Euclidian distance between this vector and the specified vector.
	 * Assumes both vectors have the same size.
	 * 
	 * @param vector
	 *            A vector to find the distance to.
	 * @return Distance between the vectors.
	 */
	public float distance(ColourVector vector) {
		double sum = 0.0;
		for (int i = 0; i < components.length; i++) {
			double x = components[i] - vector.components[i];
			sum += x * x;
		}
		return (float) Math.sqrt(sum);
	}
	
	public int size() {
		return components.length;
	}
}
