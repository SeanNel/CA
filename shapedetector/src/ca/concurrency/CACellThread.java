package ca.concurrency;

import java.util.concurrent.ArrayBlockingQueue;

import ca.CACell;

/**
 * Updates CACells in a separate thread.
 * 
 * @author Sean
 */
public class CACellThread extends Thread {
	/** The CAThreadServer that coordinates this thread. */
	protected CAThreadServer server;
	/** When this 'cell' is encountered, signals this thread to stop. */
	protected static final CACell end = new CACell();
	/** A queue that accepts a CACell to process or the 'end' singleton. */
	protected ArrayBlockingQueue<CACell> pending;

	/**
	 * Number of slots for waiting cells.
	 * <p>
	 * Needs at least 2 slots, one for the cell to process, one for the end
	 * signal - otherwise the server first has to wait for this thread to finish
	 * processing the cell. More than 2 won't make a difference unless the
	 * thread server is redesigned.
	 */
	protected static final int queueLength = 2;

	/**
	 * Constructor.
	 * 
	 * @param server
	 *            The CAThreadServer that coordinates this thread.
	 */
	public CACellThread(CAThreadServer server) {
		this.server = server;
		pending = new ArrayBlockingQueue<CACell>(queueLength);
	}

	/**
	 * Assign a cell to this thread for processing.
	 * 
	 * @param cell
	 *            Cell to update.
	 */
	public void enqueue(CACell cell) {
		try {
			// if (pending.size() - 1 >= queueLength) {
			// throw new RuntimeException(
			// "CACellThread::finish() queue length exceeded");
			// }
			pending.put(cell);
		} catch (InterruptedException e) {
			interrupted(e);
		}
	}

	public void run() {
		try {
			CACell cell;
			while ((cell = pending.take()) != end) {
				if (cell.getState() == CACell.ACTIVE) {
					server.updateCell(cell);
				}
				server.returnThread(this);
			}

			/* Frees allocated memory. */
			pending = null;
			server = null;
		} catch (InterruptedException e) {
			interrupted(e);
		}
	}

	/**
	 * Signal this thread to stop once its done with its pending jobs.
	 */
	public void finish() {
		try {
			pending.put(end);
		} catch (InterruptedException e) {
			interrupted(e);
		}
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
