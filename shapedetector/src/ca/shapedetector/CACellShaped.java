package ca.shapedetector;

import ca.CACell;
import ca.CAModel;

/**
 * Associates a cell with a shape. Meant to be extended through a subclass.
 * 
 * @author Sean
 * 
 */
public class CACellShaped extends CACell {
	/** Shape associated with this cell. */
	protected CAShape shape;

	/**
	 * Creates cell with VANNEUMAN_NEIGHBOURHOOD.
	 * 
	 * @param x
	 * @param y
	 * @param caModel
	 */
	public CACellShaped(int x, int y, CAModel caModel) {
		super(x, y, caModel);
		neighbourhoodModel = VANNEUMANN_NEIGHBOURHOOD; /* NB */
	}

	public CAShape getShape() {
		return shape;
	}

	/**
	 * Initializes cell by gathering its neighbourhood according to the chosen
	 * model and creating a new shape associated with this cell.
	 * 
	 */
	protected void init() {
		super.init();
		if (shape == null) {
			/*
			 * Cell may not be null if it has been merged with an existing shape
			 * externally.
			 */
			shape = new CAShape(this); // caModel.getSize()
			// shape.addAreaCell(this);
			((CAModelShaped) caModel).addShape(shape);
		}
	}

	/**
	 * Most efficient method of gathering the Van Neumann neighbourhood when
	 * r=1.
	 */
	@Override
	protected void meetNeighboursVanNeumann() {
		neighbours = new CACell[4];
		neighbours[0] = caModel.getCell(x, y - 1);
		neighbours[1] = caModel.getCell(x, y + 1);
		neighbours[2] = caModel.getCell(x - 1, y);
		neighbours[3] = caModel.getCell(x + 1, y);
	}

	/**
	 * Merges the shapes associated with this cell and the specified cell
	 * together.
	 * 
	 * @param cell
	 *            Cell to merge with.
	 */
	protected void merge(CACellShaped cell) {
		// Stopwatch stopwatch = new Stopwatch();

		/* What happens when it is a padding cell? */
		CAShape cShape = cell.getShape();

		if (cShape == null) {
			/*
			 * Cell has not been initialized yet, and since it is to be merged
			 * with this cell, we can save time by not having to initialize it.
			 */
			cell.setShape(shape);
			shape.addAreaCell(cell);
		} else {
			/* TODO: optimize direction based on larger/smaller shape... */
			shape.merge(cShape);
			((CAModelShaped) caModel).removeShape(cShape);
		}
		// System.out.println("Merge time: " + stopwatch.time() +
		// }
	}

	/**
	 * Sets the shape associated with this cell.
	 * 
	 * @param shape
	 */
	protected void setShape(CAShape shape) {
		this.shape = shape;
	}

	/*
	 * protected static Color adjustColour(Color colour, float factor) { float[]
	 * components = colour.getComponents(null); for (int i = 0; i <
	 * components.length; i++) { components[i] = components[i] * factor; if
	 * (components[i] < 0f) { components[i] = 0f; } else if (components[i] > 1f)
	 * { components[i] = 1f; } } return new Color(colour.getColorSpace(),
	 * components, 1f); }
	 * 
	 * protected static Color[] getColours(ArrayList<CACell> cells) { Color[]
	 * colours = new Color[cells.size()]; for (int i = 0; i < cells.size(); i++)
	 * { colours[i] = cells.get(i).getColour(); } return colours; }
	 */
}