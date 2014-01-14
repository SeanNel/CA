package ca.shapedetector.distribution;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import math.DiscreteFunction;
import ca.shapedetector.path.SDPath;

public class AreaDistribution {

	/**
	 * Gets the area distribution of the path. Greater precision should be
	 * achieved with more sectors.
	 * <p>
	 * Casts triangles subtended by the centroid of the path, with base vertices
	 * along a circle that encloses the path. Triangles were chosen because they
	 * are simple to implement, fast to calculate, and do not overlap. A list is
	 * generated of the areas enclosed by the path in each sector.
	 * <p>
	 * These data could then be autocorrelated to find the axes of symmetry.
	 * 
	 * @return The number of sectors to divide the azimuth into.
	 */
	public static DiscreteFunction getAreaDistribution(SDPath path,
			int numSectors) {
		double sweep = 2.0 * Math.PI / (double) numSectors;
		double w = path.getBounds().getWidth();
		double h = path.getBounds().getHeight();
		/*
		 * The shape will always fit inside the circle if the radius is
		 * calculated from the center (and not the center of mass).
		 */
		double radius = Math.sqrt((w * w + h * h) / 2.0);
		double sectorArea = radius * radius * Math.sin(sweep / 2.0)
				* Math.cos(sweep / 2.0);

		Area pathShape = path.getAreaPolygon();

		double[] abscissae = new double[numSectors];
		double[] ordinates = new double[numSectors];

		Point2D centroid = path.getCentroid();
		// System.out.println("***");
		for (int i = 0; i < numSectors; i++) {
			double theta = sweep * (i - 0.5);
			double x1 = centroid.getX() + radius * Math.cos(theta);
			double y1 = centroid.getY() + radius * Math.sin(theta);
			double x2 = centroid.getX() + radius * Math.cos(theta + sweep);
			double y2 = centroid.getY() + radius * Math.sin(theta + sweep);

			Path2D.Double sector = new Path2D.Double();
			sector.moveTo(centroid.getX(), centroid.getY());
			sector.lineTo(x1, y1);
			sector.lineTo(x2, y2);
			sector.closePath();

			Area sectorShape = new Area(sector);
			sectorShape.subtract(pathShape);
			SDPath projectionPath = new SDPath(sectorShape);
			double projectionArea = sectorArea - projectionPath.getArea();
			ordinates[i] = projectionArea;
			abscissae[i] = theta;
			// System.out.println("theta: " + Math.round(Math.toDegrees(sweep *
			// (i))) + ", sector area: " + Math.round(projectionArea));
		}
		return new DiscreteFunction(abscissae, ordinates);
	}

}
