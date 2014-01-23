package ca;

import helpers.Stopwatch;

import java.util.List;

import ca.concurrency.ThreadServer;
import ca.lattice.Lattice;
import ca.rules.Rule;
import exceptions.CAException;

/**
 * Cellular automaton for processing an image.
 * 
 * @author Sean
 */
public class CA<V> {
	public final static int DEFAULT_NUMTHREADS = 8;
	/** The cell lattice. */
	protected Lattice<V> lattice;
	/** Rules to apply to each cell in sequence. */
	public final List<Rule<Cell<V>>> rules;
	/**
	 * Number of additional threads to run in parallel.
	 */
	protected int numThreads;

	/**
	 * Constructor.
	 * 
	 * @param epsilon
	 *            The difference threshold expressed as a fraction. Determines
	 *            how neighbourhood cells affect this cell's state. Low values
	 *            mean that small differences between cells are ignored.
	 * @param r
	 *            Search radius. Determines the size of the neighbourhood.
	 */
	public CA(final Lattice<V> lattice, final List<Rule<Cell<V>>> rules,
			final int numThreads) {
		this.lattice = lattice;
		this.rules = rules;
		this.numThreads = numThreads;
	}

	public CA(final Lattice<V> lattice, final List<Rule<Cell<V>>> rules) {
		this(lattice, rules, DEFAULT_NUMTHREADS);
	}

	/**
	 * Applies the cell rules to each cell.
	 * 
	 * @param picture
	 *            Picture to process.
	 * @return Processed picture.
	 * @throws CAException
	 */
	public void apply() throws CAException {
		System.out.println(this.getClass().getSimpleName() + " started.");
		Stopwatch stopwatch = new Stopwatch();

		stopwatch.print("Loading complete, elapsed time: ");

		for (Rule<Cell<V>> rule : rules) {
			rule.prepare();
			boolean active = true;
			// int passes = 0;
			while (active) {
				// stopwatch.start();
				// if (numThreads==0) {
				// /* Linear method */
				// for (Cell cell : lattice) {
				// rule.update(cell);
				// }
				// } else {
				/* Multithreaded method */
				ThreadServer<Cell<V>> threadServer = new ThreadServer<Cell<V>>(
						rule, lattice, numThreads);
				active = threadServer.run();
				// }
				lattice.complete();
				// passes++;
				// if (active || passes > 0) {
				// System.out.println(" pass #" + passes +
				// ", elapsed time: "
				// + stopwatch.time() + " ms");
				// }
			}
			rule.complete();
		}
	}

	/**
	 * Gets the cell lattice.
	 * 
	 * @return
	 */
	public Lattice<V> getLattice() {
		return lattice;
	}

	/**
	 * Gets the number of threads.
	 * 
	 * @return
	 */
	public int getNumThreads() {
		return numThreads;
	}

}