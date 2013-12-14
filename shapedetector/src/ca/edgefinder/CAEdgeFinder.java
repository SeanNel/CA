package ca.edgefinder;

import ca.CACell;
import ca.CAModel;

/**
 * Finds the edges (points of high contrast) in an image. This class may soon become
 * deprecated in favour of the CAShapeDetector.
 * 
 * @author Sean
 */
public class CAEdgeFinder extends CAModel {
	public CAEdgeFinder(float epsilon, int r) {
		super(epsilon, r);
	}

	public CACell newCell(int x, int y, CAModel caModel) {
		return new EdgeFinderCell(x, y, caModel);
	}
}
