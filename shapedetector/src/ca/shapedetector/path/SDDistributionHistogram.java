package ca.shapedetector.path;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import math.CartesianVector;

public class SDDistributionHistogram {
	/**
	 * Gets the gradient distribution of this path.
	 * <p>
	 * Specifically, this is the angle between points on the path and the
	 * tangent to a circle centered on the centroid of the path. Thus for a
	 * circular path, the gradient distribution is constant.
	 */
	protected static double[] getGradientDistribution(SDPath sdPath) {
		List<Double> gradientHistogram = new ArrayList<Double>();
		List<Double> spacingData = new ArrayList<Double>();

		SDPathIterator pathIterator1 = sdPath.iterator();
		SDPathIterator pathIterator2 = sdPath.iterator();
		pathIterator2.next();

		while (pathIterator2.hasNext()) {
			gatherGradientDistributionData(pathIterator1, pathIterator2,
					gradientHistogram, spacingData, sdPath.getCentroid());
		}

		/* Includes the last point along the path as well. */
		pathIterator2 = sdPath.iterator();
		gatherGradientDistributionData(pathIterator1, pathIterator2,
				gradientHistogram, spacingData, sdPath.getCentroid());

		/* TODO Fix me! */
		// gradientHistogram = balanceGradientDistribution(gradientHistogram,
		// spacingData);

		return getArray(gradientHistogram);
	}

	private static void gatherGradientDistributionData(
			SDPathIterator pathIterator1, SDPathIterator pathIterator2,
			List<Double> gradientData, List<Double> spacingData,
			double[] centroid) {
		double[] coordinates1 = pathIterator1.next();
		double[] coordinates2 = pathIterator2.next();

		double[] a = { coordinates1[0], coordinates1[1] };
		double[] b = { coordinates2[0], coordinates2[1] };
		double[] vector = CartesianVector.getRelative(a, b);

		double distance = CartesianVector.getLength(vector);
		if (distance == 0) {
			/* TODO fix this */
			return;
		}
		spacingData.add(distance);

		// double gradient = CartesianVector.getAngle(vector);

		double[] vector2 = CartesianVector.getRelative(centroid, b);
		double gradient = getGradient(vector, vector2);

		gradientData.add(gradient);
	}

	private static double getGradient(double[] v1, double[] v2) {
		/* Y is negative because it increases from top to bottom. */
		v1[1] *= -1;
		v2[1] *= -1;

		double gradient = CartesianVector.getAngle(v1)
				- CartesianVector.getAngle(v2);
		if (gradient < 0) {
			gradient += 2.0 * Math.PI;
		}
		gradient -= (Math.PI / 2.0);
		return gradient;
	}

	/**
	 * This method ensures that the gradient distribution data is spaced evenly
	 * according to the distance between the sampled points. TODO: Fix me!
	 * 
	 * @param gradientData
	 * @param spacingData
	 * @return
	 */
	private static List<Double> balanceGradientDistribution(
			List<Double> gradientData, List<Double> spacingData) {
		double minSpacing = Double.MAX_VALUE;
		for (Double distance : spacingData) {
			if (distance < minSpacing) {
				minSpacing = distance;
			}
		}

		List<Double> gradientHistogram = new ArrayList<Double>(
				gradientData.size());

		Iterator<Double> gradientIterator = gradientData.iterator();
		Iterator<Double> spacingIterator = spacingData.iterator();

		while (gradientIterator.hasNext()) {
			Double gradientElement = gradientIterator.next();
			Double weight = spacingIterator.next() / minSpacing;

			for (Double i = 0.0; i < weight; i++) {
				gradientHistogram.add(gradientElement);
			}
		}

		return gradientHistogram;
	}

	private static double[] getArray(List<Double> list) {
		double[] array = new double[list.size()];
		Iterator<Double> iterator = list.iterator();
		for (int i = 0; i < array.length; i++) {
			array[i] = iterator.next();
		}
		return array;
	}

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
	protected static double[] getAreaDistribution(SDPath sdPath, int numSectors) {
		double sweep = 2.0 * Math.PI / (double) numSectors;
		double w = sdPath.path.getBounds2D().getWidth();
		double h = sdPath.path.getBounds2D().getHeight();
		/*
		 * The shape will always fit inside the circle if the radius is
		 * calculated from the center (and not the center of mass).
		 */
		double radius = Math.sqrt((w * w + h * h) / 2.0);
		double sectorArea = radius * radius * Math.sin(sweep / 2.0)
				* Math.cos(sweep / 2.0);

		Area pathShape = new Area(sdPath.path);

		double[] symmetryHistogram = new double[numSectors];

		// System.out.println("***");
		for (int i = 0; i < numSectors; i++) {
			double theta = sweep * (i - 0.5);
			double x1 = sdPath.centroid[0] + radius * Math.cos(theta);
			double y1 = sdPath.centroid[1] + radius * Math.sin(theta);
			double x2 = sdPath.centroid[0] + radius * Math.cos(theta + sweep);
			double y2 = sdPath.centroid[1] + radius * Math.sin(theta + sweep);

			Path2D.Double sector = new Path2D.Double();
			sector.moveTo(sdPath.centroid[0], sdPath.centroid[1]);
			sector.lineTo(x1, y1);
			sector.lineTo(x2, y2);
			sector.closePath();

			Area sectorShape = new Area(sector);
			sectorShape.subtract(pathShape);
			SDPath projectionPath = new SDPath(sectorShape);
			double projectionArea = sectorArea - projectionPath.getArea();
			symmetryHistogram[i] = projectionArea;
			// System.out.println("theta: " + Math.round(Math.toDegrees(sweep *
			// (i))) + ", sector area: " + Math.round(projectionArea));
		}
		return symmetryHistogram;
	}
}
