package ca.rules.blob;

import graphics.SDPanel;

import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;

import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.path.SDArea;
import ca.shapedetector.path.SDPath;
import ca.shapedetector.shapes.AbstractShape;
import ca.shapedetector.shapes.UnknownShape;

/**
 * Displays all the found blobs larger than 4x4 cells on the screen, in turn.
 */
public class BlobDisplayRule extends BlobRule {
	final SDPanel panel;

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

		Area area = SDArea.makeArea(blob.getAreaCells());
		// area = SDPath.fillGaps(area);
		SDPath path = new SDPath(area);
		AbstractShape shape = new UnknownShape(path);

		Rectangle2D bounds = shape.getPath().getBounds();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
		panel.display(shape);

		// helpers.Input.waitForSpace();
	}
}
