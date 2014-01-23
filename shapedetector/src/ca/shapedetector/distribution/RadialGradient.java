package ca.shapedetector.distribution;

import java.awt.geom.Point2D;

/**
 * The values obtained should be similar to taking the differentiation of the graph
 * obtained from RadialDistance.
 * 
 * @author Sean
 */
public class RadialGradient extends AbsoluteGradient {

	protected double getValue(final Point2D o, final Point2D a, final Point2D b) {
//		 double x = o.getX() - b.getX();
//		 double y = o.getY() - b.getY();

		double theta = getGradient(o, b) + Math.PI / 2d;
		return theta;
	}
}
