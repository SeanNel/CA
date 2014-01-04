package ca.rules.blob;

import ca.shapedetector.CABlob;
import ca.shapedetector.CAShapeDetector;

/* TODO extend CACellRule */
public abstract class CABlobRule {
	protected CAShapeDetector ca;

	/**
	 * Constructor.
	 * 
	 * @param ca
	 */
	public CABlobRule(CAShapeDetector ca) {
		this.ca = ca;
	}

	/**
	 * Applies the rule and updates the specified blob.
	 * 
	 * @param blob
	 */
	public void update(CABlob blob) {
		/** Method stub. */
	}
	
	public String toString() {
		return this.getClass().getSimpleName();
	}
}
