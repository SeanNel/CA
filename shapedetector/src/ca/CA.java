package ca;

import helpers.Stopwatch;

import java.util.List;

import std.Picture;
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
	public final List<Rule<V>> rules;
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
	public CA(Lattice<V> lattice, List<Rule<V>> rules, int numThreads) {
		this.lattice = lattice;
		this.rules = rules;
		this.numThreads = numThreads;
	}

	public CA(Lattice<V> lattice, List<Rule<V>> rules) {
		this(lattice, rules, DEFAULT_NUMTHREADS);
	}

	/**
	 * Updates cells until they are all done (that is, until they all become
	 * inactive).
	 * 
	 * @param picture
	 *            Picture to process.
	 * @return Processed picture.
	 */
	public Picture apply(Picture picture) {
		try {
			System.out.println(this.getClass().getSimpleName() + " started.");
			Stopwatch stopwatch = new Stopwatch();

			stopwatch.print("Loading complete, elapsed time: ");

			for (Rule<V> rule : rules) {
				rule.start();
				boolean active = true;
				// int passes = 0;
				while (active) {
					// stopwatch.start();
					// if (debug) {
					// /* Linear method */
					// for (Cell cell : lattice) {
					// rule.update(cell);
					// }
					// } else {
					/* Multithreaded method */
					ThreadServer<V> threadServer = new ThreadServer<V>(rule,
							lattice, numThreads);
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
				rule.end();
			}
		} catch (CAException e) {
			handleException(e);
		}

		return lattice.getResult();
	}

	/**
	 * Handles exceptions.
	 * 
	 * @param e
	 */
	protected void handleException(CAException e) {
		e.printStackTrace();
		System.exit(0);
	}

	/**
	 * Gets the output image.
	 */
	public Picture getResult() {
		return lattice.getResult();
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