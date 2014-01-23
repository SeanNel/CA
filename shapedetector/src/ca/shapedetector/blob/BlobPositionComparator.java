package ca.shapedetector.blob;

import java.util.Comparator;

/**
 * For debugging. Sorts found blobs in a logical sequence, according to their
 * positions in the image.
 * 
 * @author Sean
 * 
 */
public class BlobPositionComparator<V> implements
		Comparator<BlobPositionComparator<V>> {
	public final Blob<V> blob;

	/**
	 * Constructor.
	 * 
	 * @param blob
	 */
	public BlobPositionComparator(final Blob<V> blob) {
		this.blob = blob;
	}

	@Override
	public int compare(final BlobPositionComparator<V> arg0,
			final BlobPositionComparator<V> arg1) {
		double x1 = arg0.blob.getBounds().getMinX();
		double y1 = arg0.blob.getBounds().getMinY();

		double x2 = arg1.blob.getBounds().getMinX();
		double y2 = arg1.blob.getBounds().getMinY();

		if (x1 == x2 && y1 == y2) {
			return 0;
		} else if (y1 < y2) {
			return -1;
		} else {
			if (x1 < x2) {
				return -1;
			} else {
				return 1;
			}
		}
	}

}