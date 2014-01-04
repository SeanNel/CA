package ca.shapedetector.shapes;

import java.awt.Shape;
import java.awt.geom.Rectangle2D;

import math.DiscreteFunction;

import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.path.SDDistributionHistogram;
import ca.shapedetector.path.SDPath;

public class SDQuadrilateral extends SDShape {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 0.2;

	private double length;
	private double width;

	public SDQuadrilateral() {
		super();
	}

	public SDQuadrilateral(SDPath path) {
		super(path);
		getProperties();
	}

	public SDQuadrilateral(SDShape shape) {
		super(shape);
	}

	protected void loadRelatedShapes() {
		// relatedShapes.add(new SDRectangle(picture));
	}

	protected SDShape identify(SDShape shape) {
		shape.getPath().display(CAShapeDetector.shapeFrame);

		SDQuadrilateral identity = getIdentity(shape);
		if (identity == null) {
			return null;
		}

		identity.getPath().display(CAShapeDetector.identityFrame);

		int x = 0;
		int y = 0;
		CAShapeDetector.shapeFrame.setLocation(x, y);
		y += CAShapeDetector.shapeFrame.getHeight() + 20;
		CAShapeDetector.identityFrame.setLocation(x, y);

		double match = identity.compare(shape);
		if (1.0 - match < tolerance) {
			SDQuadrilateral quad = new SDQuadrilateral(shape);
			shape = SDRectangle.identify(quad);
			return shape;
		} else {
			return null;
		}
	}

	public double compare(SDShape shape) {
		/*
		 * Smooth at most distances, but accuracy becomes bad for long, thin
		 * shapes.
		 */
		 int comparisonType =
		 SDDistributionHistogram.RADIAL_DISTANCE_DISTRIBUTION;
//		int comparisonType = SDDistributionHistogram.RADIAL_GRADIENT_DISTRIBUTION;

		double[] f = SDDistributionHistogram.getGradientDistribution(this,
				comparisonType);
		double[] g = SDDistributionHistogram.getGradientDistribution(shape,
				comparisonType);
		
		/* Normalizes the graphs */
		f = DiscreteFunction.fit(f, 100);
		g = DiscreteFunction.fit(g, 100);

		double correlation = DiscreteFunction.getCorrelation(f, g);

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

	protected SDQuadrilateral getIdentity(SDShape shape) {
		/* Creates identity shape and compares the two. */
		Shape identityShape = SDDistributionHistogram.getPolygon(shape, 4);
		if (identityShape == null) {
			return null;
		}
		SDPath path = new SDPath(identityShape);
		SDQuadrilateral identity = new SDQuadrilateral(path);
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
