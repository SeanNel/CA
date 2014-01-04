package ca.rules.blob;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Area;

import ca.shapedetector.CABlob;
import ca.shapedetector.CAShapeDetector;
import ca.shapedetector.path.SDPath;

/**
 * Displays all the found blobs larger than 4x4 cells on the screen, in turn.
 */
public class CABlobDrawRule extends CABlobRule {
	/** Shape's fill colour. */
	protected Color fillColour;
	/** Shape's outline colour. */
	protected Color outlineColour;
	/** Shape's centroid colour. */
	protected Color centroidColour;
	/** Shape's text label colour. */
	protected Color labelColour;
	protected static final Font DEFAULT_FONT = new Font("SansSerif",
			Font.PLAIN, 10);
	/** Shape's text label font. */
	protected Font font;

	protected static Graphics2D graphics;

	public CABlobDrawRule(CAShapeDetector ca) {
		super(ca);
		graphics = ca.getPicture().getImage().createGraphics();
		defaultColours();
	}

	protected void defaultColours() {
		fillColour = new Color(230, 245, 230, 100);
		outlineColour = Color.red;
		centroidColour = Color.magenta;
		labelColour = Color.blue;
		font = DEFAULT_FONT;
	}

	public void update(CABlob blob) {
		if (blob.getArea() < 16) {
			return;
		}

		Area area = SDPath.makeArea(blob.getAreaCells());
		// area = SDPath.fillGaps(area);
		SDPath path = new SDPath(area);

		path.draw(graphics, outlineColour, fillColour);
		// Input.waitForSpace();
	}
}
