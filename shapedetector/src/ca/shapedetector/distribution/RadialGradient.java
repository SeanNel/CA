package ca.shapedetector.distribution;

import java.awt.geom.Point2D;

public class RadialGradient extends AbsoluteGradient {

	protected double getValue(Point2D o, Point2D a, Point2D b) {
		// double x = o.getX() - b.getX();
		// double y = o.getY() - b.getY();

		// double theta = getAngle(x, y);
		// if (theta > Math.PI) {
		// theta -= Math.PI;
		// }
		double theta = getGradient(a, b);
		return theta;
	}
}
