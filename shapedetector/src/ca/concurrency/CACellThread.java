package ca.concurrency;

import ca.CACell;

/**
 * Updates CACells in a separate thread.
 * 
 * @author Sean
 */
public class CACellThread extends Thread {
	/** The CAThreadServer that coordinates this thread. */
	protected CAThreadServer server;

	/**
	 * Constructor.
	 * 
	 * @param server
	 *            The CAThreadServer that coordinates this thread.
	 */
	public CACellThread(CAThreadServer server) {
		this.server = server;
	}

	@Override
	public void run() {
//		 this.setPriority(MAX_PRIORITY);
		CACell cell;
		while ((cell = server.dequeue()) != null) {
			server.updateCell(cell);
		}
		server.clockOut(this);
		/* Frees some allocated memory. */
		server = null;
	}
}
