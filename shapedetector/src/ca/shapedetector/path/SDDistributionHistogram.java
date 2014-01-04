package ca.shapedetector.path;

import java.awt.Shape;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import math.CartesianVector;
import math.DiscreteFunction;
import math.fit.FitPoly;
import ca.shapedetector.shapes.SDShape;

public class SDDistributionHistogram {
	/* Enumerates the distribution types. */
	/**
	 * This gets the angle between tangents to points along the path and the
	 * tangent to a circle centered on the centroid of the path. Thus for a
	 * circular path, the gradient distribution is constant.
	 */
	public static final int RADIAL_GRADIENT_DISTRIBUTION = 0;
	/**
	 * This gets the angle of points along the path, relative to the x-axis.
	 */
	public static final int ABSOLUTE_GRADIENT_DISTRIBUTION = 1;
	/**
	 * This divides the path into triangular sectors around the centroid and
	 * gets the area of these sectors.
	 */
	public static final int RADIAL_AREA_DISTRIBUTION = 10;
	/**
	 * This gets the distance between the centroid and points along the path.
	 */
	public static final int RADIAL_DISTANCE_DISTRIBUTION = 20;

	/** Should give a straight-line graph */
	public static final int RELATIVE_DISTANCE = 30;
	/** Should give a straight-line graph that wraps around at y=2Pi */
	public static final int RADIAL_ANGLE = 31;

	protected double[] getNormalizedDistribution(SDShape shape,
			int distributionType) {
		double min;
		double max;

		double[] distributionData = getGradientDistribution(shape,
				distributionType);

		switch (distributionType) {
		case SDDistributionHistogram.RADIAL_AREA_DISTRIBUTION:
		case SDDistributionHistogram.RADIAL_DISTANCE_DISTRIBUTION:
			double f[] = distributionData.clone();
			DiscreteFunction.absoluteValue(f);

			min = DiscreteFunction.minimum(f);
			max = DiscreteFunction.maximum(f);
			break;
		case SDDistributionHistogram.ABSOLUTE_GRADIENT_DISTRIBUTION:
		case SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION:
			DiscreteFunction.medianFilter(distributionData, 5);

			min = 0.0;
			max = Math.PI;
			break;
		default:
			min = 0.0;
			max = 1.0;
		}

		/* TODO Fix me! or maybe not... */
		// gradientHistogram = balanceGradientDistribution(gradientHistogram,
		// spacingData);

		distributionData = DiscreteFunction.fit(distributionData, 100);

		/* Normalizes the data. */
		DiscreteFunction.add(distributionData, -min);
		DiscreteFunction.times(distributionData, 1.0 / (max - min));

		return distributionData;
	}

	public static double[] getGradientDistribution(SDShape shape, int type) {
		if (type == RADIAL_AREA_DISTRIBUTION) {
			return getAreaDistribution(shape, 16);
		}

		List<Double> gradientHistogram = new ArrayList<Double>();
		List<Double> spacingData = new ArrayList<Double>();

		SDPathIterator pathIterator1 = shape.iterator();
		SDPathIterator pathIterator2 = shape.iterator();
		pathIterator2.next();

		while (pathIterator2.hasNext()) {
			gatherGradientData(type, pathIterator1, pathIterator2,
					gradientHistogram, spacingData, shape.getCentroid());
		}

		/* Includes the last point along the path as well. */
		pathIterator2 = shape.iterator();
		gatherGradientData(type, pathIterator1, pathIterator2,
				gradientHistogram, spacingData, shape.getCentroid());

		return getArray(gradientHistogram);
	}

	private static void gatherGradientData(int type,
			SDPathIterator pathIterator1, SDPathIterator pathIterator2,
			List<Double> gradientData, List<Double> spacingData,
			double[] centroid) {
		double[] coordinates1 = pathIterator1.next();
		double[] coordinates2 = pathIterator2.next();

		double[] a = { coordinates1[0], coordinates1[1] };
		double[] b = { coordinates2[0], coordinates2[1] };
		double[] dR = CartesianVector.getRelative(a, b);

		double distance = CartesianVector.getLength(dR);
		if (distance == 0) {
			/* TODO fix this */
			return;
		}
		spacingData.add(distance);

		double gradient = 0.0;
		double[] radial = CartesianVector.getRelative(centroid, b);

		switch (type) {
		case RADIAL_DISTANCE_DISTRIBUTION:
			gradient = CartesianVector.getLength(radial);
			break;
		case RADIAL_GRADIENT_DISTRIBUTION:
			gradient = getGradient(dR, radial);
			break;
		case ABSOLUTE_GRADIENT_DISTRIBUTION:
			gradient = CartesianVector.getAngle(dR);
			break;
		// case AREA_DISTRIBUTION:
		// break;
		case RELATIVE_DISTANCE:
			gradient = distance;
			break;
		case RADIAL_ANGLE:
			gradient = CartesianVector.getAngle(radial);
			break;
		}

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
	@SuppressWarnings("unused")
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
	public static double[] getAreaDistribution(SDShape shape, int numSectors) {
		double sweep = 2.0 * Math.PI / (double) numSectors;
		double w = shape.getBounds().getWidth();
		double h = shape.getBounds().getHeight();
		/*
		 * The shape will always fit inside the circle if the radius is
		 * calculated from the center (and not the center of mass).
		 */
		double radius = Math.sqrt((w * w + h * h) / 2.0);
		double sectorArea = radius * radius * Math.sin(sweep / 2.0)
				* Math.cos(sweep / 2.0);

		Area pathShape = shape.getAreaPolygon();

		double[] symmetryHistogram = new double[numSectors];

		double[] centroid = shape.getCentroid();
		// System.out.println("***");
		for (int i = 0; i < numSectors; i++) {
			double theta = sweep * (i - 0.5);
			double x1 = centroid[0] + radius * Math.cos(theta);
			double y1 = centroid[1] + radius * Math.sin(theta);
			double x2 = centroid[0] + radius * Math.cos(theta + sweep);
			double y2 = centroid[1] + radius * Math.sin(theta + sweep);

			Path2D.Double sector = new Path2D.Double();
			sector.moveTo(centroid[0], centroid[1]);
			sector.lineTo(x1, y1);
			sector.lineTo(x2, y2);
			sector.closePath();

			Area sectorShape = new Area(sector);
			sectorShape.subtract(pathShape);
			SDPath projectionPath = new SDPath(sectorShape);
			double projectionArea = sectorArea - projectionPath.calculateArea();
			symmetryHistogram[i] = projectionArea;
			// System.out.println("theta: " + Math.round(Math.toDegrees(sweep *
			// (i))) + ", sector area: " + Math.round(projectionArea));
		}
		return symmetryHistogram;
	}

	/**
	 * Returns a polygon with n vertices/sides that is based on the given data.
	 * 
	 * @return Polygon with n sides.
	 * */
	public static Shape getPolygon(SDShape shape, int n) {
		/* Gets the distribution histogram. */
		/*
		 * Smooth at most distances, but accuracy becomes bad for long, thin
		 * shapes.
		 */
		int comparisonType = SDDistributionHistogram.RADIAL_DISTANCE_DISTRIBUTION;

		// int comparisonType =
		// SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION;
		double[] distributionData = SDDistributionHistogram
				.getGradientDistribution(shape, comparisonType);
		/* Normalizes data and filters noise. Noise filtering is NB */
		int resolution = distributionData.length;
		distributionData = DiscreteFunction.fit(distributionData, 100);

		/* Use with RADIAL_GRADIENT_DISTRIBUTION to reduce nooise. */
//		DiscreteFunction.meanFilter(distributionData, 5);

		/*
		 * Use with RADIAL_DISTANCE_DISTRIBUTION. Fit to n'th polynomial to
		 * eliminate noise. Creates problems with periodicity, because the start
		 * of the graph is detected as a local maximum when it is really part of
		 * a crest at the end of the graph.
		 */
		distributionData = DiscreteFunction.regress(distributionData,
				distributionData.length / n * 3); // n * 2 + 1 // n * 16

		// int p = (int) Math.floor((double) distributionData.length / (double)
		// n / 4.0);
		// DiscreteFunction.bandPass(distributionData, p, p + 1);

		/* Debugging chart */
		// double[] f = distributionData.clone();
		// DiscreteFunction.dataset.removeAllSeries();
		// DiscreteFunction.dataset.addSeries(DiscreteFunction.distributionChart
		// .getSeries(f, "f(x)"));
		//
		// DiscreteFunction.differentiate(f);
		// DiscreteFunction.dataset.addSeries(DiscreteFunction.distributionChart
		// .getSeries(f, "d/dx f(x)"));
		//
		// DiscreteFunction.differentiate(f);
		// DiscreteFunction.dataset.addSeries(DiscreteFunction.distributionChart
		// .getSeries(f, "d/dx 2 f(x)"));
		//
		// if (DiscreteFunction.distributionChart.isFocusable()) {
		// DiscreteFunction.distributionChart.setVisible(true);
		// }

		/* Gets a list of local maxima. */
		Map<Integer, Double> maxima = DiscreteFunction.maxima(distributionData);

		Set<Entry<Integer, Double>> entrySet = maxima.entrySet();
		System.out.println("***");
		for (Entry<Integer, Double> entry : entrySet) {
			System.out.println("x=" + entry.getKey() + ", y="
					+ entry.getValue());
		}
		if (maxima.size() < n) {
			return null;
		}

		Iterator<Entry<Integer, Double>> iterator = maxima.entrySet()
				.iterator();

		double[][] outline = shape.getOutline();
		Integer vertex = iterator.next().getKey();
		vertex = (int) Math.floor((double) vertex
				/ (double) distributionData.length * (double) resolution);

		double x = outline[vertex][0];
		double y = outline[vertex][1];

		Path2D path = new Path2D.Double();
		path.moveTo(x, y);

		for (int i = 1; i < n; i++) {
			if (!iterator.hasNext()) {
				return null;
			}
			vertex = iterator.next().getKey();
			vertex = (int) Math.floor((double) vertex
					/ (double) distributionData.length * (double) resolution);
			x = outline[vertex][0];
			y = outline[vertex][1];
			path.lineTo(x, y);
		}

		iterator = maxima.entrySet().iterator();
		vertex = iterator.next().getKey();
		x = outline[vertex][0];
		y = outline[vertex][1];
		path.lineTo(x, y);
		path.closePath();

		return path;
	}
}
