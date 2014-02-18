package utils;

import org.apache.commons.math3.util.FastMath;
import org.apache.commons.math3.util.MathUtils;

public class AngleUtils {

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
				return FastMath.PI / 2.0;
			} else {
				return 1.5 * FastMath.PI;
			}
		} else if (y == 0) {
			if (x >= 0) {
				return 0;
			} else {
				return FastMath.PI;
			}
		} else {
			double angle = FastMath.atan(FastMath.abs(y / x));
			if (x < 0 && y > 0) {
				angle = FastMath.PI - angle;
			} else if (x < 0 && y < 0) {
				angle = FastMath.PI + angle;
			} else if (x > 0 && y < 0) {
				angle = 2.0 * FastMath.PI - angle;
			}
			return angle;
		}
	}

	/**
	 * Gets a representative angle from the angle specified. The angle returned
	 * is [0,90 deg)
	 * 
	 * @param theta
	 * @return
	 */
	public static double representativeAngle(final double theta) {
		double a = MathUtils.reduce(theta, FastMath.PI, 0);

		if (a <= 0.5d * FastMath.PI) {
			return a;
		} else if (a <= FastMath.PI) {
			return FastMath.PI - a;
		} else if (a <= 1.5d * FastMath.PI) {
			return a - FastMath.PI;
		} else {
			return 2d * FastMath.PI - a;
		}
	}
}
