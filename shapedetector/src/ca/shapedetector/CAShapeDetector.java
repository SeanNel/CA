package ca.shapedetector;

import graphics.ColourCompare;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import ca.CACell;

import std.Picture;

/**
 * Finds the edges, ensuring that edges are closed loops, while also
 * accomplishing much of the work needed to detect shapes as well.
 * 
 * @author Sean
 */
public class CAShapeDetector extends CAShaped {
	/**
	 * Colour that outline cells turn to when they become inactive, that is the
	 * foreground colour of the output image.
	 */
	public final static Color OUTLINE_COLOUR = new Color(0, 0, 0);

	List<Object> detectedShapes;
	static int I = 0;

	public CAShapeDetector(float epsilon) {
		super(epsilon);
	}

	public void setPicture(Picture picture) {
		detectedShapes = new ArrayList<Object>();
		super.setPicture(picture);
	}

	public Picture pointOutShapes(Picture picture) {
		System.out.println("I: " + I);
		filter();
		return picture;
	}

	public void filter() {
		TreeSet<CAShape> shapes = new TreeSet<CAShape>();
		for (CAShape shape : this.shapes) {
			if (shape.getArea() >= minArea) {
				shapes.add(shape);
			}
		}
		this.shapes = shapes;
	}

	/**
	 * Updates the specified cell.
	 * <p>
	 * On the 1st pass, finds shapes (groups of cells of similar colour).
	 * <p>
	 * On the 2nd pass, to find the edge cells, checks whether cell neighbours
	 * any cells belonging to a different shape.
	 */
	@Override
	public void updateCell(CACell cell) {
		super.updateCell(cell);
		CACell[] neighbourhood = cell.getNeighbourhood();
		for (int i = 0; i < neighbourhoodSize; i++) {
			CACell neighbour = neighbourhood[i];
			if (neighbour == paddingCell) {
				break;
			}
			if (passes == 0) {
				float difference = ColourCompare.getDifference(getColour(cell),
						getColour(neighbour));
				if (difference < epsilon) {
					// mergeShapes(getShape(cell), getShape(neighbour));
					enqueueMerger(cell, neighbour);
					I++;
				}
				cell.setState(CACell.ACTIVE);
			} else if (getShape(cell) != getShape(neighbour)) {
				setOutline(cell);
				cell.setState(CACell.INACTIVE);
			} else {
				setArea(cell);
				cell.setState(CACell.INACTIVE);
			}
		}
	}

	/**
	 * Make this cell an outline cell (black).
	 * <p>
	 * Note that the apparent thickness of edges is irrelevant. Each shape's
	 * outline is determined by a single layer of cells and this is ensured by
	 * the algorithm.
	 */
	public void setOutline(CACell cell) {
		setColour(cell, OUTLINE_COLOUR);
		getShape(cell).addOutlineCell(cell);
	}

	/**
	 * Makes this cell an area cell (white).
	 * <p>
	 * Note that the shape's areaCell collection already contains this cell, so
	 * do not add it again.
	 */
	public void setArea(CACell cell) {
		setColour(cell, QUIESCENT_COLOUR);
	}
}
