package ca.concurrency;

import java.util.Iterator;

import ca.rules.Rule;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * Coordinates threads that update cells concurrently.
 * 
 * @author Sean
 */
public class ThreadServer<V> {
	public final static int DEFAULT_NUMTHREADS = 8;

	/** The rule to apply to queued items. */
	protected final Rule<V> rule;
	/** The source to queue items from. */
	// protected final Iterable<V> source;
	/** The queue iterator. */
	protected Iterator<V> iterator;

	/** Number of threads to create. */
	protected final int numThreads;
	/** The number of finished threads. */
	protected int clockedInThreads;

	/**
	 * Creates server with default number of threads.
	 * 
	 * @throws NullParameterException
	 */
	public ThreadServer(final Rule<V> rule, final Iterable<V> source)
			throws NullParameterException {
		this(rule, source.iterator(), DEFAULT_NUMTHREADS);
	}

	public ThreadServer(final Rule<V> rule, final Iterator<V> iterator)
			throws NullParameterException {
		this(rule, iterator, DEFAULT_NUMTHREADS);
	}

	public ThreadServer(final Rule<V> rule, final Iterable<V> source,
			int numThreads) throws NullParameterException {
		this(rule, source.iterator(), numThreads);
	}

	/**
	 * Creates server with specified number of threads.
	 * 
	 * @param rule
	 * @param source
	 * @param numThreads
	 *            Number of threads to create.
	 * @throws NullParameterException
	 */
	public ThreadServer(Rule<V> rule, final Iterator<V> iterator,
			final int numThreads) throws NullParameterException {
		if (rule == null) {
			throw new NullParameterException("rule");
		}
		this.rule = rule;
		// this.source = source;
		this.iterator = iterator;
		this.numThreads = numThreads;
	}

	/**
	 * Gets an object for processing.
	 * 
	 * @return A object from the queue or null if the queue is empty.
	 */
	public V dequeue() {
		synchronized (iterator) {
			if (iterator.hasNext()) {
				return iterator.next();
			} else {
				return null;
			}
		}
	}

	/**
	 * Updates specified object.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void update(final V object) {
		try {
			rule.update(object);
		} catch (CAException e) {
			handleException(e);
		}
	}

	/**
	 * Updates the queued cells. TODO: return a boolean when it is due to
	 * repeat, and not just false all the time...
	 * 
	 * @return true when there are active cells remaining.
	 */
	public boolean run() {
		clockedInThreads = 0;

		for (int i = 0; i < numThreads; i++) {
			CAThread<V> thread = new CAThread<V>(this);
			thread.start();
		}

		synchronized (this) {
			try {
				while (clockedInThreads < numThreads) {
					wait();
				}
			} catch (InterruptedException e) {
				interrupted(e);
			}
		}
		return false;
	}

	/**
	 * Called when a thread has finished.
	 * 
	 * @param caCellThread
	 */
	public synchronized void clockOut(final CAThread<V> thread) {
		clockedInThreads++;
		notify();
	}

	/**
	 * Handles exceptions.
	 * 
	 * @param e
	 */
	protected void handleException(final CAException e) {
		e.printStackTrace();
		System.exit(0);
	}

	/**
	 * Handles interruption exceptions.
	 * 
	 * @param e
	 */
	protected void interrupted(final InterruptedException e) {
		Thread.currentThread().interrupt();
		throw new RuntimeException("Unexpected interruption");
	}
}
