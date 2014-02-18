package ca;

import rules.Rule;
import rules.RuleApplicator;

public abstract class AbstractLattice<V> implements Lattice<V> {
	private static final RuleApplicator<Cell> ruleExecutor = new RuleApplicator<Cell>();

	@Override
	public void apply(final Iterable<Cell> cells,
			final Iterable<Rule<Cell>> rules) throws Exception {
		this.apply(cells, rules, true);
	}

	public void apply(final Iterable<Cell> cells,
			final Iterable<Rule<Cell>> rules, final boolean verbose)
			throws Exception {
		for (Rule<Cell> rule : rules) {
			ruleExecutor.apply(cells, rule, verbose);
			complete();
		}
	}

	protected void complete() {
		/* Subclasses may extend this. */
	}
}
