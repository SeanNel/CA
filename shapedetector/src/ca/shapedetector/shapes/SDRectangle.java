package ca.shapedetector.shapes;

public class SDRectangle extends SDQuadrilateral {
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected static double tolerance = 4.0E-4;

	private double length;
	private double width;

	public SDRectangle() {
		super();
	}

	protected void loadRelatedShapes() {
	}

	public SDRectangle(SDQuadrilateral shape) {
		super(shape);
	}

	/**
	 * Returns a rectangle if detected, otherwise returns the quadrilateral
	 * given as parameter. TODO: change from square detection to rectangle
	 * detection
	 * 
	 * @param shape
	 * @return
	 */
	protected static SDShape identify(SDQuadrilateral quad) {
		double delta = Math.abs(quad.getLength() - quad.getWidth());
		double area = quad.getLength() * quad.getWidth();
		// System.out.println(delta / area);
		if (delta / area < tolerance) {
			return new SDRectangle(quad);
		} else {
			return quad;
		}
	}

	protected void getProperties() {
		// Rectangle2D rectangle = getBounds();
		//
		// length = rectangle.getWidth();
		// width = rectangle.getHeight();
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
