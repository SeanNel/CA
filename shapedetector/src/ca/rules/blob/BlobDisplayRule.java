package ca.rules.blob;

import graphics.SDPanel;

import java.awt.geom.Area;

import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.SDShape;

/**
 * Displays all the found blobs larger than 4x4 cells on the screen, in turn.
 */
public class BlobDisplayRule extends BlobRule {
	SDPanel panel;

	public BlobDisplayRule(BlobMap blobMap, SDPanel panel) {
		super(blobMap);
		this.panel = panel;
	}

	public void start() {
		panel.setVisible(true);
	}

	public void update(Blob blob) {
		if (blob.getArea() < 16) {
			return;
		}

		Area area = SDPath.makeArea(blob.getAreaCells());
		// area = SDPath.fillGaps(area);
		SDPath path = new SDPath(area);
		SDShape shape = new SDShape(path);

		double[] dimensions = shape.getDimensions();
		panel.reset((int) dimensions[0], (int) dimensions[1]);
		panel.display(shape);

		// Input.waitForSpace();
	}
}
