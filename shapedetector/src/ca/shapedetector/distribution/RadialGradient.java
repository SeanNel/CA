package ca.shapedetector.distribution;

import java.awt.geom.Point2D;

public class RadialGradient extends Distribution {

	protected double getValue(Point2D o, Point2D a, Point2D b) {
		double x = o.getX() - b.getX();
		double y = o.getY() - b.getY();

		return getAngle(x,y);
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
			// if (x < 0 && y > 0) {
			// angle = Math.PI - angle;
			// } else if (x < 0 && y < 0) {
			// angle = Math.PI + angle;
			// } else if (x > 0 && y < 0) {
			// angle = 2.0 * Math.PI - angle;
			// }
			return angle;
		}
	}
}
