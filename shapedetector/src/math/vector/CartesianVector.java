package math.vector;

public class CartesianVector {
	/**
	 * Gets the vector from v1 to v2.
	 * 
	 * @param v1
	 *            2-dimensional array of coordinates to 1st vector.
	 * @param v2
	 *            2-dimensional array of coordinates to 2nd vector.
	 * @return
	 */
	public static double[] getRelative(double[] v1, double[] v2) {
		double x = v2[0] - v1[0];
		double y = v2[1] - v1[1];
		double coordinates[] = { x, y };
		return coordinates;
	}

	/**
	 * Gets the polar angle of the vector, in radians. The range is 0 to 2Pi.
	 * 
	 * @param v
	 * @return
	 */
	public static double getAngle(double[] v) {
		double x = v[0];
		double y = v[1];

		if (x == 0) {
			if (y >= 0) {
				return Math.PI / 2.0;
			} else {
				return 1.5 * Math.PI;
			}
		} else if (y == 0) {
			if (x >= 0) {
				return 0;
			} else {
				return Math.PI;
			}
		} else {
			double angle = Math.atan(Math.abs(y / x));
			if (x < 0 && y > 0) {
				angle = Math.PI - angle;
			} else if (x < 0 && y < 0) {
				angle = Math.PI + angle;
			} else if (x > 0 && y < 0) {
				angle = 2.0 * Math.PI - angle;
			}
			return angle;
		}
	}

	public static double getLength(double[] vector) {
		return Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1]);
	}
}
