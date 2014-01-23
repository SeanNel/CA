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
public class BlobDisplayRule<V extends Blob<V>> extends BlobRule<V> {
	final SDPanel panel;

	public BlobDisplayRule(final BlobMap<V> blobMap, final SDPanel panel) {
		super(blobMap);
		this.panel = panel;
	}

	@Override
	public void prepare() {
		panel.setVisible(true);
	}

	@Override
	public void update(final Blob<V> blob) {
		if (blob.getArea() < 16) {
			return;
		}

		Area area = (new SDArea<V>()).makeArea(blob.getAreaCells());
		// area = SDPath.fillGaps(area);
		SDPath path = new SDPath(area);
		AbstractShape shape = new UnknownShape(path);

		Rectangle2D bounds = shape.getPath().getBounds();
		panel.reset((int) bounds.getWidth(), (int) bounds.getHeight());
		panel.display(shape);

		// helpers.Input.waitForSpace();
	}

}
