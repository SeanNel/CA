package ca.rules.blob;

import ca.shapedetector.BlobMap;
import ca.shapedetector.ShapeList;
import ca.shapedetector.blob.Blob;
import ca.shapedetector.shapes.RootShape;
import exceptions.CAException;

/**
 * Identifies blobs as shapes.
 */
public class BlobIdentifierRule<V> extends BlobRule<V> {
	protected final RootShape<V> shapeDetector = new RootShape<V>();
	protected final ShapeList shapeList;

	public BlobIdentifierRule(final BlobMap<V> blobMap,
			final ShapeList shapeList) {
		super(blobMap);
		this.shapeList = shapeList;
	}

	@Override
	public void update(final Blob<V> blob) throws CAException {
		if (blob.getOutlineCells().size() > 16) {
			shapeList.addShape(shapeDetector.identify(blob));
		}
	}
}
