package helpers;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.math3.util.MathUtils;

public class Misc {

	/**
	 * Makes an array of the specified list.
	 * 
	 * @param list
	 * @return
	 */
	public static double[] toArray(final List<Double> list) {
		int n = list.size();
		double[] array = new double[n];
		Iterator<Double> iterator = list.iterator();
		for (int i = 0; i < n; i++) {
			array[i] = iterator.next();
		}
		return array;
	}

	/**
	 * Gets the angle of the vector (x,y).
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	public static double getAngle(final double x, final double y) {
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

	/**
	 * Gets a representative angle from the angle specified. The angle returned
	 * is [0,90 deg)
	 * 
	 * @param a
	 * @return
	 */
	public static double representativeAngle(final double theta) {
		double a = MathUtils.reduce(theta, Math.PI, 0);

		if (a <= 0.5d * Math.PI) {
			return a;
		} else if (a <= Math.PI) {
			return Math.PI - a;
		} else if (a <= 1.5d * Math.PI) {
			return a - Math.PI;
		} else {
			return 2d * Math.PI - a;
		}
	}
}
