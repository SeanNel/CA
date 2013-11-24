package ca.cornerfinder;

import java.util.ArrayList;

import ca.CACell;
import ca.CAModel;

public class CACornerFinder extends CAModel {
	ArrayList<CACell> corners;

	public CACornerFinder(float epsilon, int r) {
		super(epsilon, r);
		corners = new ArrayList<CACell>();
	}

	public ArrayList<CACell> getCorners() {
		// If no corners have been found, most likely we have not updated yet.
		if (corners.size() == 0)
			update();
		return corners;
	}
}
