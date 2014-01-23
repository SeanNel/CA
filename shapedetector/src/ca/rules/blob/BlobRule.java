package ca.rules.blob;

import ca.rules.Rule;
import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;
import exceptions.CAException;

public abstract class BlobRule<V> implements Rule<Blob<V>> {
	protected final BlobMap<V> blobMap;

	/**
	 * Constructor.
	 * 
	 * @param blobMap
	 */
	public BlobRule(final BlobMap<V> blobMap) {
		this.blobMap = blobMap;
	}

	@Override
	public void prepare() throws CAException {
		/* Method stub. */
	}

	// @Override
	// public void update(Blob blob) throws CAException {
	// /* Method stub. */
	// }

	@Override
	public void complete() throws CAException {
		/* Method stub. */
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
