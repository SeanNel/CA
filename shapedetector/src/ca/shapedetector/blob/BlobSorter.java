package ca.shapedetector.blob;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * For debugging. Sorts found blobs in a logical sequence, according to their
 * positions in the image.
 * 
 * @author Sean
 * 
 */
public class BlobSorter implements Comparable<BlobSorter> {
	public final Blob blob;

	public BlobSorter(Blob blob) {
		this.blob = blob;
	}

	@Override
	public int compareTo(BlobSorter arg0) {
		double x1 = (blob.getBoundaries()[0][1] - blob.getBoundaries()[0][0]) / 2.0;
		double y1 = (blob.getBoundaries()[1][1] - blob.getBoundaries()[1][0]) / 2.0;

		double x2 = (arg0.blob.getBoundaries()[0][1] - arg0.blob
				.getBoundaries()[0][0]) / 2.0;
		double y2 = (arg0.blob.getBoundaries()[1][1] - arg0.blob
				.getBoundaries()[1][0]) / 2.0;

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

	public static Set<Blob> sortBlobs(Set<Blob> shapes) {
		BlobSorter[] shapeSorter = new BlobSorter[shapes.size()];
		Iterator<Blob> iterator = shapes.iterator();
		for (int i = 0; i < shapes.size(); i++) {
			shapeSorter[i] = new BlobSorter(iterator.next());
		}
		Arrays.sort(shapeSorter);
		Set<Blob> sortedShapes = new LinkedHashSet<Blob>();
		for (BlobSorter s : shapeSorter) {
			sortedShapes.add(s.blob);
		}
		return sortedShapes;
	}

}