package ca.shapedetector;

import java.util.ArrayList;

import ca.CAModel;

/**
 * TODO: description
 * 
 * @author Sean
 * 
 */
public class CAModelShaped extends CAModel {
	/** The collection of shapes found in the source image. */
	protected volatile ArrayList<CAShape> shapes;
	/** Shapes with areas smaller than this will be ignored. */
	protected int minArea = 16;

	public CAModelShaped(float epsilon) {
		super(epsilon, 1);
		shapes = new ArrayList<CAShape>();
	}

	/** Add the specified shape to the list of shapes found. */
	public synchronized void addShape(CAShape shape) {
		shapes.add(shape);
	}

	/**
	 * Removes the specified shape from the list of shapes found. Called when
	 * two shapes merge.
	 */
	public synchronized void removeShape(CAShape shape) {
		shapes.remove(shape);
	}

	/**
	 * Gets the list of shapes found.
	 */
	public ArrayList<CAShape> getShapes() {
		return shapes;
	}

	/**
	 * Print a summary of the detected shapes.
	 */
	public void printSummary() {
		System.out.println("Number of shapes: " + shapes.size());
		System.out.println(shapes);
	}
}