package ca.shapedetector.path;

import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.PathIterator;
import java.awt.geom.Rectangle2D;
import java.util.Iterator;
import java.util.List;

import ca.Cell;

/**
 * Provides some static methods for manipulating Area objects.
 * 
 * @author Sean
 */
public class SDArea<V> {
	/**
	 * Constructs a path from a list of cells describing the area.
	 * 
	 * @param cells
	 * @return
	 */
	public Area makeArea(final List<Cell<V>> cells) {
		Area area = new Area();
		Iterator<Cell<V>> cellIterator = cells.iterator();
		while (cellIterator.hasNext()) {
			Cell<V> cell = cellIterator.next();
			int[] coordinates = cell.getCoordinates();
			Rectangle2D rectangle = new Rectangle2D.Double(coordinates[0],
					coordinates[1], 1, 1);
			area.add(new Area(rectangle));
		}

		return area;
	}

	/**
	 * Fills in gaps of an area.
	 * 
	 * @param cells
	 * @return
	 */
	public static Area fillGaps(final Area area) {
		Area areaCopy = new Area(area);
		PathIterator pathIterator = areaCopy.getPathIterator(null);
		double[] coordinates = new double[6];
		Path2D path = new Path2D.Double();
		path.moveTo(coordinates[0], coordinates[1]);

		while (!pathIterator.isDone()) {
			int type = pathIterator.currentSegment(coordinates);
			switch (type) {
			case PathIterator.SEG_MOVETO:
				path.closePath();
				Area segmentArea = new Area(path);
				areaCopy.add(segmentArea);
				path = new Path2D.Double();
				path.moveTo(coordinates[0], coordinates[1]);
				break;
			case PathIterator.SEG_LINETO:
				path.lineTo(coordinates[0], coordinates[1]);
				break;
			}

			pathIterator.next();
		}
		return area;
	}
}
