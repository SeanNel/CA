package ca.rules.cacell;

import ca.CA;
import ca.CACell;

public abstract class CACellRule {
	protected CA ca;

	/**
	 * Constructor.
	 * 
	 * @param ca
	 */
	public CACellRule(CA ca) {
		this.ca = ca;
	}

	/**
	 * Applies the rule and updates the specified cell.
	 * 
	 * @param cell
	 */
	public void update(CACell cell) {
		/** Method stub. */
	}
}
