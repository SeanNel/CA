package ca.concurrency;

/**
 * Updates objects in a separate thread.
 * 
 * @author Sean
 */
public class CAThread<V> extends Thread {
	/** The CAThreadServer that coordinates this thread. */
	protected final ThreadServer<V> server;

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
		V object;
		while ((object = server.dequeue()) != null) {
			server.update(object);
		}
		server.clockOut(this);
	}
}
