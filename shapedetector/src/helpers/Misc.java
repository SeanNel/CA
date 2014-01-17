package helpers;

import java.util.Iterator;
import java.util.List;

public class Misc {

	public static double[] toArray(List<Double> list) {
		int n = list.size();
		double[] array = new double[n];
		Iterator<Double> iterator = list.iterator();
		for (int i = 0; i < n; i++) {
			array[i] = iterator.next();
		}
		return array;
	}

	public static double getAngle(double x, double y) {
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

	public static double representativeAngle(double d1) {
		// d1 = MathUtils.normalizeAngle(d1, Math.PI);
		while (d1 < 0) {
			d1 += 2d * Math.PI;
		}
		while (d1 >= 2d * Math.PI) {
			d1 -= 2d * Math.PI;
		}

		if (d1 <= 0.5d * Math.PI) {
			return d1;
		} else if (d1 <= Math.PI) {
			return Math.PI - d1;
		} else if (d1 <= 1.5d * Math.PI) {
			return d1 - Math.PI;
		} else {
			return 2d * Math.PI - d1;
		}

	}
}
