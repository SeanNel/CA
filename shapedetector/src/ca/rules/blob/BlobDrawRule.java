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
public class BlobDrawRule<V extends Blob<V>> extends BlobRule<V> {
	protected final SDPanel panel;

	public BlobDrawRule(final SDPanel panel, final BlobMap<V> blobMap) {
		super(blobMap);
		this.panel = panel;
	}

	@Override
	public void update(final Blob<V> blob) {
		if (blob.getArea() < 16) {
			return;
		}

		Area area = (new SDArea<V>()).makeArea(blob.getAreaCells());
		// area = SDPath.fillGaps(area);
		SDPath path = new SDPath(area);

		panel.draw(path);
		// Input.waitForSpace();
	}
}
