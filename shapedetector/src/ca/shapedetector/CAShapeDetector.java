package ca.shapedetector;

import java.util.ArrayList;
import std.Picture;
import ca.CACell;
import ca.CAModel;

/**
 * Finds the edges, ensuring that edges are closed loops, while also
 * accomplishing much of the work needed to detect shapes as well.
 * 
 * @author Sean
 */
public class CAShapeDetector extends CAModelShaped {
	ArrayList<Object> detectedShapes;

	public CAShapeDetector(float epsilon) {
		super(epsilon);
	}

	protected CACell newCell(int x, int y, CAModel caModel) {
		return new ShapeDetectorCell(x, y, caModel);
	}

	public void setPicture(Picture picture) {
		detectedShapes = new ArrayList<Object>();
		super.setPicture(picture);
	}

	public void process() {
		super.process();
		 filter();
	}

	public void filter() {
		ArrayList<CAShape> shapes = new ArrayList<CAShape>();
		for (CAShape shape : this.shapes) {
			if (shape.getArea() >= minArea) {
				shapes.add(shape);
			}
		}
		this.shapes = shapes;
	}
}
