package ca.rules.protoshape;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ca.CA;
import ca.CACell;
import ca.shapedetector.CAProtoShape;
import ca.shapedetector.CAShapeDetector;

/**
 * Assimilates the specified shape into a neighbouring shape if it is too small
 * to be of importance.
 */
public class CAProtoShapeAssimilatorRule extends CAProtoShapeRule {
	/**
	 * Shapes with areas smaller than this will be assimilated into larger
	 * shapes.
	 */
	protected int minArea = 25;

	public CAProtoShapeAssimilatorRule(CAShapeDetector ca) {
		super(ca);
	}

	public void update(CAProtoShape protoShape) {
		if (protoShape.getArea() < minArea) {
			/*
			 * May be null if this shape has already been merged from this
			 * loop...
			 */
			if (protoShape.getOutlineCells() != null) {
				CAProtoShape newProtoShape = assimilate(protoShape);
				/*
				 * Recursively merge shapes together until there are no more
				 * small shapes remaining.
				 */
				if (newProtoShape != null) {
					update(newProtoShape);
				}
			}
		}
	}

	/**
	 * Assimilates the specified protoShape into a neighbouring protoShape and
	 * returns the resulting protoShape.
	 * 
	 * @param protoShape
	 * @return The merged protoShape.
	 */
	protected CAProtoShape assimilate(CAProtoShape protoShape) {
		/** A set of all the shapes next to this one. */
		Set<CAProtoShape> neighbouringShapes = new HashSet<CAProtoShape>();
		/*
		 * Gathers all the shapes next to this one. Duplicates would slow down
		 * the step after this which iterates through all these shapes, which is
		 * why neighbouringShapes is a set and not a list.
		 */
		for (CACell cell : protoShape.getOutlineCells()) {
			List<CACell> neighbourhood = cell.getNeighbourhood();
			for (CACell neighbour : neighbourhood) {
				if (neighbour != cell && neighbour != CA.paddingCell) {
					CAProtoShape neighbouringShape = ca.getShape(neighbour);
					if (neighbouringShape != protoShape) {
						neighbouringShapes.add(neighbouringShape);
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
		CACell repCell = protoShape.getAreaCells().get(0);
		for (CAProtoShape neighbouringShape : neighbouringShapes) {
			Color colour1 = getShapeAverageColour(protoShape);
			Color colour2;
			// synchronized (neighbouringShape) {
			colour2 = getShapeAverageColour(neighbouringShape);
			CACell repCell2 = neighbouringShape.getAreaCells().get(0);
			// }
			float difference = ColourCompare.getDifference(colour1, colour2);
			if (difference < minDifference) {
				minDifference = difference;
				similarCell = repCell2;
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
		return ca.mergeCells(repCell, similarCell);
	}

	/**
	 * Gets the average colour of the shape.
	 * <p>
	 * This is calculated here instead of from CAProtoShape because for that to
	 * be possible, a reference to the CA has to be stored in each shape. When
	 * there are thousands of shapes, this extra memory use can become
	 * significant.
	 * 
	 * @return Average colour.
	 */
	protected Color getShapeAverageColour(CAProtoShape shape) {
		synchronized (shape) {
			if (shape.getValidate()) {
				Color[] colours = new Color[shape.getAreaCells().size()];
				for (int i = 0; i < colours.length; i++) {
					colours[i] = ca.getColour(shape.getAreaCells().get(i));
				}
				shape.setColour(ColourCompare.averageColour(colours));
			}
			return shape.getColour();
		}
	}
}
