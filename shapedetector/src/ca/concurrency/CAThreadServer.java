package ca.concurrency;

import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;

import ca.CA;
import ca.CACell;

/**
 * Coordinates threads that update cells concurrently.
 * 
 * @author Sean
 */
public class CAThreadServer extends Thread {
	/** Reference to the CA that spawned this server. */
	CA ca;
	/** States whether the server is waiting for input. */
	protected volatile boolean active;
	/** Special cell that signals the server to stop waiting for input. */
	protected static final CACell end = new CACell();

	/** Number of threads to create. */
	protected int numThreads = 8;
	/** Array of all the threads created by this server. */
	protected CACellThread[] threads;
	/** Queue of cells waiting to be assigned to threads. */
	protected LinkedBlockingQueue<CACell> pending;
	/** Queue of threads that are available to be assigned cells to update. */
	protected LinkedBlockingQueue<CACellThread> threadPool;

	/**
	 * Creates server with default number of threads.
	 */
	public CAThreadServer(CA ca) {
		this.ca = ca;
		init(numThreads);
	}

	/**
	 * Creates server with specified number of threads.
	 * 
	 * @param numThreads
	 *            Number of threads to create.
	 */
	public CAThreadServer(CA ca, int numThreads) {
		this.ca = ca;
		init(numThreads);
	}

	/**
	 * Initializes server with the specified number of threads.
	 * 
	 * @param numThreads
	 *            Number of threads to create.
	 */
	protected void init(int numThreads) {
		this.numThreads = numThreads;

		active = true;
		pending = new LinkedBlockingQueue<CACell>(numThreads);
		threadPool = new LinkedBlockingQueue<CACellThread>(numThreads);
		threads = new CACellThread[numThreads];

		/*
		 * Setting the server priority high while setting cell threads to low
		 * improves performance a little (in the order of a few dozen ms).
		 */
		try {
			for (int i = 0; i < numThreads; i++) {
				CACellThread thread = new CACellThread(this);
				thread.start();
				this.setPriority(MIN_PRIORITY);
				threads[i] = thread;
				threadPool.put(thread);
			}
		} catch (InterruptedException e) {
			interrupted(e);
		}
		this.setPriority(MAX_PRIORITY);
	}

	/**
	 * Adds the specified cell to the queue to be processed later.
	 * 
	 * @param cell
	 *            Cell to be processed.
	 */
	public void enqueue(CACell cell) {
		try {
			pending.put(cell);
		} catch (InterruptedException e) {
			interrupted(e);
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
	 * Assigns cells from the queue to threads for processing.
	 * <p>
	 * Synchronizing on the server causes the main thread to wait for all cell
	 * threads to finish before continuing.
	 */
	@Override
	public synchronized void run() {
		try {
			/* Assign pending cells to threads. */
			while (active || !pending.isEmpty()) {
				CACell cell = pending.take();
				if (cell == end) {
					break;
				}
				CACellThread thread = threadPool.take();
				thread.enqueue(cell);
			}

			/* Signal threads to finish what they are doing. */
			for (CACellThread thread : threads) {
				thread.finish();
			}

			/* Wait for all threads to finish. */
			LinkedList<CACellThread> finishedThreads = new LinkedList<CACellThread>();
			while (finishedThreads.size() < numThreads) {
				/* Should also check that the thread has in fact finished. */
				CACellThread thread = threadPool.take();
				finishedThreads.add(thread);
			}

			/* Free allocated memory for garbage collection. */
			pending = null;
			threads = null;
			threadPool = null;
		} catch (InterruptedException e) {
			interrupted(e);
		}
	}

	/**
	 * Return thread to the thread pool where it is available again to be
	 * assigned more work.
	 * 
	 * @param thread
	 *            Thread to return to thread pool.
	 */
	public void returnThread(CACellThread thread) {
		try {
			threadPool.put(thread);
		} catch (InterruptedException e) {
			interrupted(e);
		}
	}

	/**
	 * Signals this server to stop waiting for input.
	 */
	public void finish() {
		active = false;
		try {
			pending.put(end);
		} catch (InterruptedException e) {
			interrupted(e);
		}
	}

	/**
	 * States whether this server is waiting for input.
	 * 
	 * @return Waiting state.
	 */
	public boolean isActive() {
		return active;
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
