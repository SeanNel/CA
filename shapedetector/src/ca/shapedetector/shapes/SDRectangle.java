package ca.shapedetector.shapes;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import math.DiscreteFunction;

import std.Picture;

import ca.shapedetector.path.SDDistributionHistogram;
import ca.shapedetector.path.SDPath;

public class SDRectangle extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.1;

	private double length;
	private double width;

	public SDRectangle(Picture picture) {
		super(picture);
	}

	public SDRectangle(SDPath path, Picture picture) {
		super(path, picture);
		getProperties();
	}

	public SDRectangle(SDShape shape) {
		super(shape);
	}

	protected void loadRelatedShapes() {
		// relatedShapes.add(new SDSquare(picture));
	}

	protected SDShape identify(SDShape shape) {
		SDRectangle identity = getIdentity(shape);
		if (identity == null) {
			return null;
		}
		identity.outlineColour = Color.magenta;
		identity.drawShape();

		double match = identity.compare(shape);

		if (1.0 - match < tolerance) {
			SDRectangle rectangle = new SDRectangle(shape);
			shape = SDSquare.identify(rectangle);
			return shape;
		} else {
			return null;
		}
	}

	public double compare(SDShape shape) {
		int comparisonType = SDDistributionHistogram.RADIAL_DISTANCE_DISTRIBUTION;

		double[] f = SDDistributionHistogram.getGradientDistribution(this,
				comparisonType);
		double[] g = SDDistributionHistogram.getGradientDistribution(shape,
				comparisonType);
		double correlation = DiscreteFunction.getCorrelation(f, g);

		/* Normalizes the graphs */
		/*
		 * TODO: WHY does the identity shape's countour end prematurely? It must
		 * be an off-by-one error in a loop somewhere.
		 */
//		f = DiscreteFunction.fit(f, 100);
//		g = DiscreteFunction.fit(g, 100);

//		System.out.println(correlation);
//		DiscreteFunction.dataset.removeAllSeries();
//		DiscreteFunction.dataset.addSeries(DiscreteFunction.distributionChart
//				.getSeries(f, "Identity shape"));
//		DiscreteFunction.dataset.addSeries(DiscreteFunction.distributionChart
//				.getSeries(g, "Unidentified shape"));
//		if (DiscreteFunction.distributionChart.isFocusableWindow()) {
//			DiscreteFunction.distributionChart.setVisible(true);
//		}

		return correlation;
	}

	protected SDRectangle getIdentity(SDShape shape) {
		/* Creates identity shape and compares the two. */
		Shape identityShape = SDDistributionHistogram.getPolygon(shape, 4);
		if (identityShape == null) {
			return null;
		}
		SDPath path = new SDPath(identityShape);
		SDRectangle identity = new SDRectangle(path, picture);
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
