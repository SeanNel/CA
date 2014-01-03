package ca.rules.cacell;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.CA;
import ca.CACell;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;

/**
 * Assimilates small shapes into larger ones of similar colour.
 * <p>
 * TODO: fix sync issues
 */
public class CAShapeAssimilatorRule extends CACellRule {
	protected CAShapeDetector ca;
	/**
	 * Shapes with areas smaller than this will be assimilated into larger
	 * shapes.
	 */
	protected int minArea = 100;// 25;
	public static int I = 0;
	protected Hashtable<CAProtoShape, Color> shapeColours;

	public CAShapeAssimilatorRule(CAShapeDetector ca) {
		super(ca);
		this.ca = ca;
		shapeColours = new Hashtable<CAProtoShape, Color>();
	}

	public void update(CACell cell) {
		/* Assumes that cell has a Van Neumann neighbourhood, with r=1 */
		CAProtoShape protoShape = ca.getProtoShape(cell);

		// synchronized (protoShape) {
		if (protoShape.getArea() < minArea) {
			CAProtoShape newProtoShape = assimilate(cell);
			/* The shape colour has become outdated. */
			if (newProtoShape != null) {
				shapeColours.remove(newProtoShape);
			}
			/*
			 * instead of calculating average over all cells, only add
			 * contribution of the new cells..?
			 */
		}
		/*
		 * Recursively merge shapes together until there are no more small
		 * shapes remaining.
		 */
		// if (newProtoShape != null && newProtoShape != protoShape)
		// {
		// update(newProtoShape);
		// }
	}

	/**
	 * Assimilates the specified protoShape into a neighbouring protoShape and
	 * returns the resulting protoShape.
	 * 
	 * @param protoShape
	 * @return The merged protoShape.
	 */
	protected CAProtoShape assimilate(CACell repCell) {
		/** A set of cells representing all the shapes next to this one. */
		Set<CACell> shapeRepresentatives = new HashSet<CACell>();

		CAProtoShape protoShape = ca.getProtoShape(repCell);
		Color colour1 = getColour(protoShape);

		List<CACell> cells = new LinkedList<CACell>(protoShape.getAreaCells());
		/*
		 * Gathers all the shapes next to this one. Duplicates would slow down
		 * the step after this which iterates through all these shapes, which is
		 * why neighbouringShapes is a set and not a list.
		 */
		for (CACell cell : cells) {
			List<CACell> neighbourhood = cell.getNeighbourhood();
			for (CACell neighbour : neighbourhood) {
				if (neighbour != cell && neighbour != CA.paddingCell) {
					CAProtoShape neighbouringShape = ca
							.getProtoShape(neighbour);
					if (neighbouringShape != protoShape) {
						shapeRepresentatives.add(neighbour);
					}
				}
			}
		}

		/** This gives the least difference to a neighbouring shape. */
		float minDifference = 2f;
		/**
		 * A representative shape from the neighbouring shape most similar to
		 * this shape.
		 */
		CACell similarCell = null;

		/*
		 * Finds a representative cell from the shape next to this one that is
		 * most similar to this shape.
		 */
		for (CACell neighbour : shapeRepresentatives) {
			// System.out.print(I++ + ".");
			CAProtoShape neighbouringShape = ca.getProtoShape(neighbour);
			float difference = 1f;
			Color colour2 = null;
			synchronized (neighbouringShape) {
				if (neighbouringShape.getAreaCells() != null) {
					colour2 = getColour(neighbouringShape);
				}
			}
			if (colour2 != null) {
				difference = ColourCompare.getDifference(colour1, colour2);

				if (difference < minDifference) {
					minDifference = difference;
					similarCell = neighbour;
				}
			}
		}

		/*
		 * getShapeAverageColour can be expensive, so instead we can may compare
		 * a representative cell from this shape to a representative cell of the
		 * neighbouring shape. This behaves unpredictably however, since the
		 * choice of representative cell is arbitrary and changes every time the
		 * program is run (even on the same image).
		 */
		// CACell repCell = protoShape.getAreaCells().get(0);
		// for (CAProtoShape neighbouringShape : neighbouringShapes) {
		// Color colour1 = getColour(repCell);
		// Color colour2;
		// // synchronized (neighbouringShape) {
		// CACell repCell2 = neighbouringShape.getAreaCells().get(0);
		// colour2 = getColour(repCell2);
		// // }
		// float difference = ColourCompare.getDifference(colour1, colour2);
		// if (difference < minDifference) {
		// minDifference = difference;
		// similarCell = repCell2;
		// }
		// }

		/*
		 * Merging representative cells of the two shapes instead of the shapes
		 * themselves helps avoid synchronization issues.
		 */
		if (similarCell == null) {
			return protoShape;
		} else {
			return ca.mergeCells(repCell, similarCell);
		}
	}

	/**
	 * Gets the mean (average) colour of the shape.
	 * <p>
	 * This is calculated here instead of from CAProtoShape because for that to
	 * be possible, a reference to the CA has to be stored in each shape. When
	 * there are thousands of shapes, this extra memory use can become
	 * significant.
	 * 
	 * @return Average colour.
	 */
	protected Color getMeanColour(Collection<CACell> cells) {
		// Collection<CACell> cells = new LinkedList<CACell>(cells);
		LinkedList<Color> colours = new LinkedList<Color>();
		for (CACell cell : cells) {
			colours.add(ca.getColour(cell));
		}
		return ColourCompare.meanColour(colours);
	}

	protected Color getColour(CAProtoShape shape) {
		Color colour = shapeColours.get(shape);
		if (colour == null) {
			colour = getMeanColour(shape.getAreaCells());
			shapeColours.put(shape, colour);
		}
		return colour;

	}
}
