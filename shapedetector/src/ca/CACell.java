package ca;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;

public class CACell {
	public final static Color QUIESCENT_COLOUR = new Color(255, 255, 255);

	// Denotes whether cell is active/inactive.
	protected int state;
	// Enumerate possible states. I chose integers instead of a boolean state in
	// case we need to add more states.
	protected final static int INACTIVE = 0; // Default state.
	protected final static int ACTIVE = 1;

	protected CAModel caModel; // A reference to the parent CAModel.

	// An array of CACells in the immediate neighbourhood, i.e. surrounding the
	// cell. I used an ArrayList instead of a regular array because I wanted to
	// be able to remove dead neighbours from calculations as we go along. A
	// custom linked list of x elements would perform even better, since an
	// ArrayList starts with space for 10 items.
	protected ArrayList<CACell> neighbours;

	// The resultant from all the neighbour vectors. This is simply the average
	// colour of all the neighbouring cells.
	// This method of storing the resultant state for each CACell is analogous
	// to having a duplicate image from before/after. This just seemed more
	// natural at the time.
	protected Color neighbourhood;

	protected int x;
	protected int y;

	public CACell(int x, int y, CAModel caModel) {
		this.x = x;
		this.y = y;
		this.caModel = caModel;

		neighbours = new ArrayList<CACell>();
	}

	// Might want to change this back to a square neighbourhood. The difference
	// is that a circle/sphere takes a little longer to initialize but gives
	// potentially better coverage for a given memory cost.
	// This method seems to be the most costly of all, so any optimizations here
	// should increase overall efficiency.
	public void meetNeighbours() {
		// Von Neuman neighbourhood: add all neighbouring cells within the given
		// radius. Begin by assuming all cells are in the square r*r centered on
		// (x,y). Then exclude the cells that are not inside the circle.
		// Another approach may be to iterate row for row and adjust the y
		// coordinate as a function of x.

		int r = caModel.getRadius();
		for (int i = x - r; i <= x + r; i++) {
			for (int j = y - r; j <= y + r; j++) {
				if (((i - x) * (i - x)) + ((j - y) * (j - y)) <= r * r) {
					CACell n = caModel.getCell(i, j);
					if (n != null) {
						// If n is null, it is one of the dead padding cells
						// around the image.
						neighbours.add(n);
					}
				}
			}
		}
	}

	protected void update() {
		for (int i = 0; i < neighbours.size(); i++) {
			CACell n = neighbours.get(i);
			if (n != null && !n.isActive()) {
				// Cell may be null if it is a padding cell.
				neighbours.remove(n);
			}
		}
		if (neighbours.size() == 0) {
			// If there's nothing left to compare this cell to, it won't change
			// any more and is isolated. It probably is not part of an edge.
			disactivate();
			return;
		}

		Color[] colours = new Color[neighbours.size()];
		for (int i = 0; i < neighbours.size(); i++) {
			colours[i] = neighbours.get(i).getColour();
		}
		neighbourhood = ColourCompare.averageColour(colours);
	}

	public void process() {
		// Child classes should override this. This is where the 'rule' is
		// defined.
	}

	public void activate() {
		// Public because we want to be able to activate cells from CAModel.
		state = ACTIVE;
	}

	protected void disactivate() {
		caModel.setPixel(x, y, QUIESCENT_COLOUR);
		state = INACTIVE;
	}

	public Color getColour() {
		return caModel.getPixel(x, y);
	}

	public boolean isActive() {
		return state == ACTIVE;
	}

	public String toString() {
		return "(CACell) x=" + x + ", y=" + y;
	}
}