package ca.rules.blob;

import ca.rules.Rule;
import ca.shapedetector.BlobMap;
import ca.shapedetector.blob.Blob;
import exceptions.CAException;

public abstract class BlobRule implements Rule<Blob> {
	protected BlobMap blobMap;

	/**
	 * Constructor.
	 * 
	 * @param blobMap
	 */
	public BlobRule(BlobMap blobMap) {
		this.blobMap = blobMap;
	}

	public void start() throws CAException {
		/* Method stub. */
	}

	public void update(Blob blob) throws CAException {
		/* Method stub. */
	}

	public void end() throws CAException {
		/* Method stub. */
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
