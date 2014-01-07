package ca.shapedetector.shapes;

import graphics.LineChartPanel;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import math.discrete.Correlation;
import math.discrete.DiscreteFunction;

import ca.shapedetector.Distribution;
import ca.shapedetector.path.SDPath;

public class Quadrilateral extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.2;

	private double length;
	private double width;

	public Quadrilateral() {
		super();
	}

	public Quadrilateral(SDPath path) {
		super(path);
		getProperties();
	}

	public Quadrilateral(SDShape shape) {
		super(shape);
	}

	protected void loadRelatedShapes() {
		// relatedShapes.add(new SDRectangle(picture));
	}

	protected SDShape identify(SDShape shape) {
		/* For debugging */
		// ShapeFrame.frame.display(shape);

		Quadrilateral identity = getIdentity(shape);
		if (identity == null) {
			return null;
		}

		/* For debugging */
		// IdentityFrame.frame.display(identity);

		double match = identity.compare(shape);
		if (1.0 - match < tolerance) {
			Quadrilateral quad = new Quadrilateral(shape);
			shape = Rectangle.identify(quad);
			return shape;
		} else {
			return null;
		}
	}

	public double compare(SDShape shape) {
		int comparisonType = Distribution.RADIAL_DISTANCE_DISTRIBUTION;
		// int comparisonType =
		// SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION;

		double[] f = Distribution.getGradientDistribution(this, comparisonType);
		double[] g = Distribution
				.getGradientDistribution(shape, comparisonType);

		f = DiscreteFunction.fit(f, 100);
		g = DiscreteFunction.fit(g, 100);

		double correlation = Correlation.getCorrelation(f, g);

		System.out.println(correlation);
		LineChartPanel
				.displayData(f, "Identity shape", g, "Unidentified shape");

		return correlation;
	}

	protected Quadrilateral getIdentity(SDShape shape) {
		/* Creates identity shape and compares the two. */
		Shape identityShape = Distribution.getPolygon(shape, 4);
		if (identityShape == null) {
			return null;
		}
		SDPath path = new SDPath(identityShape);
		Quadrilateral identity = new Quadrilateral(path);
		return identity;
	}

	protected void getProperties() {
		Rectangle2D rectangle = getBounds();

		length = rectangle.getWidth();
		width = rectangle.getHeight();
	}

	protected String getDescription() {
		return "l=" + length + ", w=" + width;
	}

	public double getLength() {
		return length;
	}

	public double getWidth() {
		return width;
	}
}
