package shapes;

import path.SDPath;

public abstract class Polygon extends AbstractShape {
	protected final static double TOLERANCE = DEFAULT_TOLERANCE;
	/** Minimum side length */
	protected double minSideLength = 3.0;

	Polygon(double tolerance) {
		super(null, tolerance);
	}

	public Polygon(final SDPath path, final double tolerance) {
		super(path, tolerance);
	}

	public Polygon(AbstractShape shape) {
		super(shape);
	}

	/**
	 * Returns a polygonal shape with n vertices/sides that approximates the
	 * specified shape.
	 * 
	 * @param shape
	 *            The shape to approximate.
	 * @param s
	 *            Number of vertices.
	 * @return Polygon with s sides.
	 * */
	public SDPath getPolygon(final AbstractShape shape, final int s) {
		// TODO: missing implementation
		return new SDPath();

		// /* Catches vertices that occur at the start point. */
		// double x0 = -1.0d;
		// double x1 = shape.getPath().getPerimeter();
		// UnivariateDifferentiableFunction f = shape
		// .getDistribution(distributionType);
		//
		// if (ShapeDetector.debug) {
		// graphics.LineChartFrame.displayData(x0, x1, f);
		// }
		//
		// int comparisonType;
		// if (distributionType == RADIAL_DISTANCE) {
		// comparisonType = CriticalPointComparator.MAXIMUM_Y;
		// } else {
		// comparisonType = CriticalPointComparator.SECOND_DERIVATIVE;
		// }
		// CriticalPoints criticalPoints = new CriticalPoints(f, x0, x1);
		// List<Double> indices =
		// criticalPoints.significantPoints(comparisonType,
		// s, minSideLength);
		//
		// // System.out.println("Critical points *** ");
		// // for (Double d : criticalPoints.criticalPoints()) {
		// // System.out.println(">>> " + d);
		// // }
		//
		// // System.out.println("Significant points *** ");
		// // for (Double d : indices) {
		// // System.out.println(">>> " + d);
		// // }
		//
		// List<Point2D> vertices = shape.getPath().getVertices(indices);
		// return new SDPath(vertices);
	}

}
