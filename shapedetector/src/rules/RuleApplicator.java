package rules;

import java.util.Collection;

import utils.Stopwatch;

public class RuleApplicator<V> {
	public static int numThreads;
	Stopwatch stopwatch = new Stopwatch();

	/**
	 * Applies the rule to each element from the source.
	 * 
	 * @param identifier
	 *            A string identifying the calling method.
	 * @param source
	 * @param rules
	 * @param numThreads
	 *            Additional threads to run in; set to 0 to only run from the
	 *            main thread.
	 * @throws Exception
	 */
	public void apply(final Iterable<V> source, final Rule<V> rule,
			final boolean verbose) throws Exception {
		stopwatch.start();
		if (verbose) {
			System.out.println(rule.toString() + " started.");
		}
		rule.prepare();
		if (numThreads == 0) {
			applyRuleLinear(source, rule);
		} else {
			/* Multithreaded method */
			applyRuleConcurrent(source, rule);
		}
		rule.complete();

		if (verbose) {
			System.out.println(rule.toString() + " elapsed time: "
					+ stopwatch.time() + " ms");
		}
	}

	public void apply(final Collection<V> source, final Rule<V> rule)
			throws Exception {
		this.apply(source, rule, true);
	}

	protected void applyRuleLinear(final Iterable<V> source, final Rule<V> rule) {
		try {
			for (V cell : source) {
				rule.update(cell);
			}
		} catch (Exception e) {
			handleException(e);
		}
	}

	protected void applyRuleConcurrent(final Iterable<V> source,
			final Rule<V> rule) {
		RuleExecutorService<V> executor = new RuleExecutorService<V>(rule,
				source, numThreads);
		executor.run();
	}

	/**
	 * Handles exceptions.
	 * 
	 * @param e
	 */
	protected void handleException(final Exception e) {
		e.printStackTrace();
		System.exit(0);
	}

}
