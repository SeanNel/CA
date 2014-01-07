package ca.concurrency;


/**
 * Updates objects in a separate thread.
 * 
 * @author Sean
 */
public class CAThread<V extends Updatable> extends Thread {
	/** The CAThreadServer that coordinates this thread. */
	protected ThreadServer<V> server;

	/**
	 * Constructor.
	 * 
	 * @param server
	 *            The CAThreadServer that coordinates this thread.
	 */
	public CAThread(ThreadServer<V> server) {
		this.server = server;
	}

	@Override
	public void run() {
		// this.setPriority(MAX_PRIORITY);
		V object;
		while ((object = server.dequeue()) != null) {
			server.update(object);
		}
		server.clockOut(this);
		/* Frees some allocated memory. */
		server = null;
	}
}
