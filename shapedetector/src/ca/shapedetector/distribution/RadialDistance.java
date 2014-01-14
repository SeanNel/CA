package ca.shapedetector.distribution;

import java.awt.geom.Point2D;

public class RadialDistance extends Distribution {

	protected double getValue(Point2D o, Point2D a, Point2D b) {
		return o.distance(b);
	}
}
