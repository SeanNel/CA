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
		additionalTime = time();
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
		return System.currentTimeMillis() - timer + additionalTime;
	}

	/**
	 * Prints time elapsed without resetting timer.
	 * 
	 * @param label
	 *            Specifies a text label to give context to the output message.
	 */
	public void printLap(String label) {
		System.out.println(label + time() + " ms");
	}

	/**
	 * Prints time elapsed without resetting timer.
	 */
	public void printLap() {
		System.out.println("time: " + time() + " ms");
	}

	/**
	 * Prints time elapsed and resets timer.
	 * 
	 * @param label
	 *            Specifies a text label to give context to the output message.
	 */
	public void print(String label) {
		printLap(label);
		start();
	}

	/**
	 * Prints time elapsed and resets timer.
	 */
	public void print() {
		printLap();
		start();
	}
}
