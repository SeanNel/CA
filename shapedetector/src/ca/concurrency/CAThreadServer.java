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
	public void enqueue(CACell cell) {
		if (cell.getState() == CACell.ACTIVE) {
			pending.add(cell);
		}
	}

	/**
	 * Gets a cell for processing.
	 * 
	 * @return A cell from the queue or null if the queue is empty.
	 */
	public CACell dequeue() {
		synchronized (pending) {
			if (!pending.isEmpty()) {
				return pending.pop();
			} else {
				return null;
			}
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

	/**
	 * Updates the queued cells.
	 */
	public void run() {
		for (int i = 0; i < numThreads; i++) {
			CACellThread thread = new CACellThread(this);
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
	}

	public synchronized void clockOut(CACellThread caCellThread) {
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
