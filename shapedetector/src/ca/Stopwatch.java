package ca;

/**
 * Determines elapsed time. Useful for measuring program performance.
 * 
 * @author Sean
 */
public class Stopwatch {
	protected long timer;

	/** Constructor. Also starts timer. */
	public Stopwatch() {
		start();
	}

	/** Starts timer. */
	public void start() {
		timer = System.currentTimeMillis();
	}

	/**
	 * Gets time elapsed.
	 * 
	 * @return Time elapsed since timer was started.
	 */
	public long time() {
		return System.currentTimeMillis() - timer;
	}

	/**
	 * Prints time elapsed without resetting timer.
	 * 
	 * @param label
	 *            Specifies a text label to give context to the output message.
	 */
	public void printLap(String label) {
		System.out
				.println(label + (System.currentTimeMillis() - timer) + " ms");
	}

	/**
	 * Prints time elapsed without resetting timer.
	 */
	public void printLap() {
		System.out.println("time: " + (System.currentTimeMillis() - timer)
				+ " ms");
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
