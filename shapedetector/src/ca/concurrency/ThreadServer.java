package ca.concurrency;

import java.util.LinkedList;

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

	/** Number of threads to create. */
	protected int numThreads = 8;
	/** Queue of cells waiting to be assigned to threads. */
	protected LinkedList<V> pending;
	protected int clockedInThreads = 0;

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
		pending = new LinkedList<V>();

		for (V object : iterable) {
			enqueue(object);
		}
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
	 * Adds the specified object to the queue to be processed later.
	 * 
	 * @param object
	 *            Object to be processed.
	 */
	public void enqueue(V object) {
		pending.add(object);
	}

	/**
	 * Gets an object for processing.
	 * 
	 * @return A object from the queue or null if the queue is empty.
	 */
	public V dequeue() {
		synchronized (pending) {
			if (!pending.isEmpty()) {
				return pending.pop();
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

		// if (drawOnCellUpdate && cell.validate) {
		// int[] coordinates = cell.getCoordinates();
		// Graphics2D graphics = pictureAfter.getImage().createGraphics();
		// graphics.setColor(pictureAfter.get(coordinates[0], coordinates[1]));
		// graphics.fillRect(coordinates[0], coordinates[1], 1, 1);
		// cell.validate = false;
		// }
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
