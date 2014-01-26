package ca.rules.cell;

import exceptions.CAException;
import graphics.ColourCompare;

import java.awt.Color;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ca.Cell;
import ca.lattice.Lattice;
import ca.neighbourhood.Neighbourhood;
import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;

/**
 * Incomplete rule. Assimilates small shapes into larger ones of similar colour.
 * <p>
 * TODO: fix sync issues
 */
public class ShapeAssimilatorRule extends CellRule<Color> {
	protected final BlobMap<Color> blobMap;
	/**
	 * Shapes with areas smaller than this will be assimilated into larger
	 * shapes.
	 */
	protected int minArea = 100;// 25;
	/** For debugging: a counter */
	public static int I = 0;
	protected Hashtable<Blob<Color>, Color> shapeColours;

	public ShapeAssimilatorRule(final Lattice<Color> lattice,
			final Neighbourhood<Color> neighbourhoodModel,
			final BlobMap<Color> blobMap) throws CAException {
		super(lattice, neighbourhoodModel);
		this.blobMap = blobMap;
		shapeColours = new Hashtable<Blob<Color>, Color>();
	}

	public void update(final Cell<Color> cell) throws CAException {
		/* Assumes that cell has a Van Neumann neighbourhood, with r=1 */
		Blob<Color> blob = blobMap.getBlob(cell);

		// synchronized (blob) {
		if (blob.getArea() < minArea) {
			Blob<Color> newblob = assimilate(cell);
			/* The shape colour has become outdated. */
			if (newblob != null) {
				shapeColours.remove(newblob);
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
		// if (newblob != null && newblob != blob)
		// {
		// update(newblob);
		// }
	}

	/**
	 * Assimilates the specified blob into a neighbouring blob and returns the
	 * resulting blob.
	 * 
	 * @param blob
	 * @return The merged blob.
	 * @throws CAException
	 */
	protected Blob<Color> assimilate(final Cell<Color> repCell) throws CAException {
		/** A set of cells representing all the shapes next to this one. */
		Set<Cell<Color>> shapeRepresentatives = new HashSet<Cell<Color>>();

		Blob<Color> blob = blobMap.getBlob(repCell);
		Color colour1 = getColour(blob);

		List<Cell<Color>> cells = new LinkedList<Cell<Color>>(
				blob.getAreaCells());
		/*
		 * Gathers all the shapes next to this one. Duplicates would slow down
		 * the step after this which iterates through all these shapes, which is
		 * why neighbouringShapes is a set and not a list.
		 */
		for (Cell<Color> cell : cells) {
			List<Cell<Color>> neighbourhood = cell.getNeighbourhood();
			for (Cell<Color> neighbour : neighbourhood) {
				if (neighbour != cell) {
					Blob<Color> neighbouringShape = blobMap.getBlob(neighbour);
					if (neighbouringShape != blob) {
						shapeRepresentatives.add(neighbour);
					}
				}
			}
		}

		/** This gives the least difference to a neighbouring shape. */
		double minDifference = 2d;
		/**
		 * A representative shape from the neighbouring shape most similar to
		 * this shape.
		 */
		Cell<Color> similarCell = null;

		/*
		 * Finds a representative cell from the shape next to this one that is
		 * most similar to this shape.
		 */
		for (Cell<Color> neighbour : shapeRepresentatives) {
			// System.out.print(I++ + ".");
			Blob<Color> neighbouringShape = blobMap.getBlob(neighbour);
			double difference = 1d;
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
		// CACell repCell = blob.getAreaCells().get(0);
		// for (CAblob neighbouringShape : neighbouringShapes) {
		// Color colour1 = getColour(repCell);
		// Color colour2;
		// // synchronized (neighbouringShape) {
		// CACell repCell2 = neighbouringShape.getAreaCells().get(0);
		// colour2 = getColour(repCell2);
		// // }
		// double difference = ColourCompare.getDifference(colour1, colour2);
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
			return blob;
		} else {
			return blobMap.mergeCells(repCell, similarCell);
		}
	}

	/**
	 * Gets the mean (average) colour of the shape.
	 * <p>
	 * This is calculated here instead of from CAblob because for that to be
	 * possible, a reference to the CA has to be stored in each shape. When
	 * there are thousands of shapes, this extra memory use can become
	 * significant.
	 * 
	 * @return Average colour.
	 */
	protected Color getMeanColour(final Collection<Cell<Color>> cells) {
		// Collection<CACell> cells = new LinkedList<CACell>(cells);
		LinkedList<Color> colours = new LinkedList<Color>();
		for (Cell<Color> cell : cells) {
			colours.add(lattice.getState(cell));
		}
		return ColourCompare.meanColour(colours);
	}

	protected Color getColour(final Blob<Color> shape) {
		Color colour = shapeColours.get(shape);
		if (colour == null) {
			colour = getMeanColour(shape.getAreaCells());
			shapeColours.put(shape, colour);
		}
		return colour;

	}
}
