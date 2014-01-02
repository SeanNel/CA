package ca.concurrency;

import java.util.LinkedList;

import ca.CA;
import ca.CACell;

/**
 * Coordinates threads that update cells concurrently.
 * 
 * @author Sean
 */
public class CAThreadServer {
	/** Reference to the CA that spawned this server. */
	CA ca;

	/** Number of threads to create. */
	protected int numThreads = 8;
	/** Array of all the threads created by this server. */
	protected CACellThread[] threads;
	/** Queue of cells waiting to be assigned to threads. */
	protected LinkedList<CACell> pending;
	protected int clockedInThreads = 0;

	/**
	 * Creates server with default number of threads.
	 */
	public CAThreadServer(CA ca) {
		this.ca = ca;
		pending = new LinkedList<CACell>();
	}

	/**
	 * Creates server with specified number of threads.
	 * 
	 * @param numThreads
	 *            Number of threads to create.
	 */
	public CAThreadServer(CA ca, int numThreads) {
		this(ca);
		this.numThreads = numThreads;
	}

	/**
	 * Adds the specified cell to the queue to be processed later.
	 * 
	 * @param cell
	 *            Cell to be processed.
	 */
	public synchronized void enqueue(CACell cell) {
		if (cell.getState() == CACell.ACTIVE) {
			pending.add(cell);
		}
	}

	public synchronized CACell dequeue() {
		if (!pending.isEmpty()) {
			return pending.pop();
		} else {
			return null;
		}
	}

	/**
	 * Updates specified cell.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void updateCell(CACell cell) {
		ca.updateCell(cell);
	}

	public void run() {
		threads = new CACellThread[numThreads];

		/*
		 * Setting the server priority high while setting cell threads to low
		 * improves performance a little (in the order of a few dozen ms).
		 */
		for (int i = 0; i < numThreads; i++) {
			CACellThread thread = new CACellThread(this);
			thread.start();
			threads[i] = thread;
		}
		// this.setPriority(MAX_PRIORITY);

		try {
			Object lock = new Object();

			synchronized (lock) {
				while (clockedInThreads < numThreads) {
					/* TODO: improve waiting mechanism based on notify() */
					lock.wait(10);
				}
			}

		} catch (InterruptedException e) {
			interrupted(e);
		}
		/* Free allocated memory for garbage collection. */
		threads = null;
	}

	public synchronized void clockOut(CACellThread caCellThread) {
		clockedInThreads++;
	}

	public boolean hasPending() {
		return !pending.isEmpty();
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
