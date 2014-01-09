package ca.concurrency;

import java.util.Iterator;

import ca.rules.Rule;
import exceptions.CAException;
import exceptions.NullParameterException;

/**
 * Coordinates threads that update objects concurrently.
 * 
 * @author Sean
 */
public class ThreadServer<V extends Updatable> {
	Rule<V> rule;
	protected Iterable<V> iterable;
	protected Iterator<V> iterator;

	/** Number of threads to create. */
	protected int numThreads = 8;
	protected int clockedInThreads;

	/**
	 * Creates server with default number of threads.
	 * 
	 * @throws NullParameterException
	 */
	public ThreadServer(Rule<V> rule, Iterable<V> iterable)
			throws NullParameterException {
		if (rule == null) {
			throw new NullParameterException("rule");
		}
		this.rule = rule;
		this.iterable = iterable;
	}

	/**
	 * Creates server with specified number of threads.
	 * 
	 * @param numThreads
	 *            Number of threads to create.
	 * @throws NullParameterException
	 */
	public ThreadServer(Rule<V> rule, Iterable<V> iterable, int numThreads)
			throws NullParameterException {
		this(rule, iterable);
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
	public void update(V object) {
		try {
			rule.update(object);
		} catch (CAException e) {
			handleException(e);
		}
	}

	protected void handleException(CAException e) {
		e.printStackTrace();
		System.exit(0);
	}

	/**
	 * Updates the queued cells. TODO: return the actual value, and not just
	 * false all the time...
	 * 
	 * @return true when there are active cells remaining.
	 */
	public boolean run() {
		clockedInThreads = 0;
		iterator = iterable.iterator();
		
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

	public synchronized void clockOut(CAThread<V> caCellThread) {
		clockedInThreads++;
		notify();
	}

	/**
	 * Exception handler.
	 * 
	 * @param e
	 */
	protected void interrupted(InterruptedException e) {
		Thread.currentThread().interrupt();
		throw new RuntimeException("Unexpected interruption");
	}
}
