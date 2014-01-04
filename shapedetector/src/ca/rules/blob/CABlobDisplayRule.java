package ca.rules.blob;

import java.awt.geom.Area;

import javax.swing.JFrame;

import ca.shapedetector.CABlob;
import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.path.SDPath;

/**
 * Displays all the found blobs larger than 4x4 cells on the screen, in turn.
 */
public class CABlobDisplayRule extends CABlobRule {
	public static final JFrame frame = new JFrame();

	public CABlobDisplayRule(CAShapeDetector ca) {
		super(ca);
	}

	public void update(CABlob blob) {
		if (blob.getArea() < 16) {
			return;
		}

		Area area = SDPath.makeArea(blob.getAreaCells());
		// area = SDPath.fillGaps(area);
		SDPath path = new SDPath(area);
		path.display(frame);
		// Input.waitForSpace();
	}
}
