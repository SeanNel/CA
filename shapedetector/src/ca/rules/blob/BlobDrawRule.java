package ca.rules.blob;

import graphics.SDPanel;

import java.awt.geom.Area;

import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.path.SDArea;
import ca.shapedetector.path.SDPath;

/**
 * Displays all the found blobs larger than 4x4 cells on the screen, in turn.
 */
public class BlobDrawRule extends BlobRule {
	protected final SDPanel panel;

	public BlobDrawRule(SDPanel panel, BlobMap blobMap) {
		super(blobMap);
		this.panel = panel;
	}

	public void update(Blob blob) {
		if (blob.getArea() < 16) {
			return;
		}

		Area area = SDArea.makeArea(blob.getAreaCells());
		// area = SDPath.fillGaps(area);
		SDPath path = new SDPath(area);

		panel.draw(path);
		// Input.waitForSpace();
	}
}
