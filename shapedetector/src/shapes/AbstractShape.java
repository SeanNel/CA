package shapes;

import graphics.SDPanel;
import graphics.SDPanelTheme;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import javax.swing.JFrame;

import path.SDPath;

import shapedetector.ShapeDetector;


/**
 * A shape derived from the outlineCells of a blob found by CAShapeDetector.
 * 
 * @author Sean
 */
public abstract class AbstractShape implements SDShape {
	protected final static double DEFAULT_TOLERANCE = 0.05d; // 0.3d;

	/** Path that defines this shape as a polygon. */
	protected final SDPath path;
	// /** The shape distribution function. */
	// protected final UnivariateDifferentiableFunction distributionFunction;
	/** Uncertainty tolerance when detecting a shape, expressed as a ratio. */
	protected final double tolerance;

	/**
	 * Constructor.
	 * 
	 * @param path
	 *            The path that describes this shape.
	 * @param distribution
	 */
	public AbstractShape(final SDPath path, final double tolerance) {
		this.path = path;
		this.tolerance = tolerance;
		// if (path != null) {
		// distributionFunction = loadShapeDistribution();
		// } else {
		// distributionFunction = null;
		// }
	}

	/**
	 * Constructor.
	 * 
	 * @param distribution
	 */
	protected AbstractShape(final double tolerance) {
		this(null, tolerance);
	}

	/**
	 * Copy constructor.
	 * 
	 * @param shape
	 */
	public AbstractShape(AbstractShape shape) {
		path = new SDPath(shape.path);
		this.tolerance = shape.tolerance;
	}

	/**
	 * Gets the path that describes this shape's outline.
	 * 
	 * @return
	 */
	public SDPath getPath() {
		return path;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "(" + this.getClass().getSimpleName() + ") [" + getDescription()
				+ ", centroid: " + path.getCentroid();
	}

	/**
	 * Gets a text label for the shape.
	 * 
	 * @return
	 */
	protected String getDescription() {
		Rectangle2D bounds = path.getBounds();
		return "w=" + bounds.getWidth() + ", h=" + bounds.getHeight();
	}

	public AbstractShape identify(final AbstractShape abstractShape) {
		if (abstractShape == null) {
			throw new NullPointerException("abstractShape");
		}

		AbstractShape shape = abstractShape;

		AbstractShape mask = getMask(shape);
		if (mask == null) {
			return null;
		}

		double match = mask.compare(shape);
		/* Input.waitforSpace() */

		if (1.0 - match < tolerance) {
			shape = mask.identifySubclass();
		}
		return shape;
	}

	protected AbstractShape getMask(final AbstractShape shape) {
		return shape;
	}

	protected AbstractShape identifySubclass() {
		return this;
	}

	/**
	 * Calculates the difference ratio between this shape and the specified
	 * shape.
	 * 
	 * @param shape
	 *            The shape to compare this shape to.
	 * @return The difference ratio.
	 */
	public double compare(final AbstractShape shape) {
		/* TODO: use Hausdorff distance (or modified HD) instead. */
		return compareByAreaDifference(shape);
	}

	static JFrame frame1 = new JFrame();
	static JFrame frame2 = new JFrame();
	static JFrame frame3 = new JFrame();

	protected double compareByAreaDifference(final AbstractShape shape) {
		Area maskAreaPolygon = getPath().getAreaPolygon();
		Area shapePolygon = shape.getPath().getAreaPolygon();
		double maskArea = getPath().getArea();
		double shapeArea = shape.getPath().getArea();

		shapePolygon.add(maskAreaPolygon);
		SDPath totalAreaPath = new SDPath(shapePolygon);
		double totalArea = totalAreaPath.getArea();

		if (ShapeDetector.debug) {
			AbstractShape bgShape = new UnknownShape(totalAreaPath);
			SDPanel.displayShape(bgShape, SDPanelTheme.BG);
			SDPanel.displayShape(shape, SDPanelTheme.DEFAULT);
			SDPanel.displayShape(shape, this, SDPanelTheme.MASK);

			// SDPanel.displayShape(bgShape, bgShape, SDPanelTheme.HIGHLIGHT);

			// display(bgShape, frame1);
			// display(shape, frame2);
			// display(this, frame3);

			frame1.setTitle("Combined");
			frame2.setTitle("Shape");
			frame3.setTitle("Mask");
		}

		/* Path either contains curved segments or crosses itself. */
		if (totalArea < shapeArea) {
			maskArea -= shapeArea - totalArea;
			totalArea = shapeArea;
		}

		// shapeClasses.add(new Triangle())
		double result = (shapeArea + maskArea - totalArea) / totalArea;
		/* Should make the comparison more forgiving for smaller shapes. */
		return result;
	}

	protected void display(AbstractShape shape, JFrame frame) {
		Rectangle2D bounds = shape.getPath().getBounds();
		SDPanel panel = new SDPanel();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
		panel.display(shape);

		frame.setContentPane(panel);
		frame.pack();
		frame.setVisible(true);
	}

}