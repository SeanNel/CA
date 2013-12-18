package ca;

/**
 * Determines elapsed time. Useful for measuring program performance.
 * 
 * @author Sean
 */
public class Stopwatch {
	/** Time when stopwatch was started. */
	protected long timer;
	/** Time that stopwatch ran for before being paused. */
	protected long additionalTime;
	/** States whether stopwatch is running. */
	protected boolean running;

	/** Constructor. Also starts timer. */
	public Stopwatch() {
		start();
	}

	/** Starts timer. */
	public void start() {
		timer = System.currentTimeMillis();
		additionalTime = 0;
		running = true;
	}

	/** Pauses timer. */
	public void pause() {
		additionalTime += time();
		running = false;
	}

	/** Unpauses timer. */
	public void unpause() {
		timer = System.currentTimeMillis();
		running = true;
	}

	/**
	 * Gets time elapsed.
	 * 
	 * @return Time elapsed since timer was started.
	 */
	public long time() {
		if (running) {
			return System.currentTimeMillis() - timer + additionalTime;
		} else {
			return additionalTime;
		}
	}

	/**
	 * Prints time elapsed without resetting timer.
	 * 
	 * @param label
	 *            Specifies a text label to give context to the output message.
	 */
	public void print(String label) {
		System.out.print(label);
		print();
	}

	/**
	 * Prints time elapsed without resetting timer.
	 */
	public void print() {
		System.out.println(time() + " ms");
	}
}
