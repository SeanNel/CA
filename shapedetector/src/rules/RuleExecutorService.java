package rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import shapedetector.ShapeDetector;

/**
 * Coordinates threads that update objects concurrently.
 * 
 * @author Sean
 */
public class RuleExecutorService<V> {
	public final static int DEFAULT_NUMTHREADS = 8;
	public final static ThreadFactory threadFactory = Executors
			.defaultThreadFactory();

	/** Number of threads to create. */
	protected final int numThreads;
	/** The rule to apply to queued items. */
	protected final Rule<V> rule;
	/** The queue iterator. */
	protected Iterator<V> iterator;

	private final class UpdateTask implements Callable<Boolean> {
		public final Boolean call() throws Exception {
			V object;
			while ((object = dequeue()) != null) {
				rule.update(object);
			}
			return false;
		}
	}

	// private final class UpdateTask implements Callable<Boolean> {
	// private final V object;
	//
	// public UpdateTask(final V object) {
	// this.object = object;
	// }
	//
	// public final Boolean call() throws Exception {
	// rule.update(object);
	// return false;
	// }
	// }

//	public static void init(final int numThreads) {
//		new RuleExecutorService<Object>(numThreads);
//	}
//
//	private RuleExecutorService(final int numThreads) {
//		this.rule = null;
//		this.iterator = null;
//		this.numThreads = numThreads;
//	}

	/**
	 * 
	 * @param rule
	 * @param source
	 * @param numThreads
	 *            Number of threads to create.
	 */
	public RuleExecutorService(Rule<V> rule, final Iterator<V> iterator,
			final int numThreads) {
		if (rule == null) {
			throw new NullPointerException("rule");
		}
		if (iterator == null) {
			throw new NullPointerException("iterator");
		}

		this.rule = rule;
		this.iterator = iterator;
		this.numThreads = numThreads;
	}

	public RuleExecutorService(final Rule<V> rule, final Iterable<V> source) {
		this(rule, source.iterator(), DEFAULT_NUMTHREADS);
	}

	public RuleExecutorService(final Rule<V> rule, final Iterator<V> iterator) {
		this(rule, iterator, DEFAULT_NUMTHREADS);
	}

	public RuleExecutorService(final Rule<V> rule, final Iterable<V> source,
			int numThreads) {
		this(rule, source.iterator(), numThreads);
	}

	/**
	 * Updates the queued cells.
	 * 
	 * @return false. (TODO: return true when there are active cells remaining.)
	 */
	public boolean run() {
		if (rule == null) {
			throw new NullPointerException("rule");
		}
		if (iterator == null) {
			throw new NullPointerException("iterator");
		}

		ExecutorService executorService = Executors.newFixedThreadPool(
				numThreads, threadFactory);
		Collection<UpdateTask> tasks = new ArrayList<UpdateTask>(numThreads);
		for (int i = 0; i < numThreads; i++) {
			tasks.add(new UpdateTask());
		}
		// while (iterator.hasNext()) {
		// tasks.add(new UpdateTask(iterator.next()));
		// }

		try {
			executorService.invokeAll(tasks);
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.HOURS);
		} catch (InterruptedException e) {
			interrupted(e);
		}

		return false;
	}

	/**
	 * Gets an object for processing.
	 * 
	 * @return A object from the queue or null if the queue is empty.
	 */
	public V dequeue() {
		synchronized (iterator) {
			if (iterator.hasNext()) {
				return iterator.next();
			} else {
				return null;
			}
		}
	}

	/**
	 * Handles exceptions.
	 * 
	 * @param e
	 */
	protected void handleException(final Exception e) {
		ShapeDetector.handleException(e);
	}

	/**
	 * Handles interruption exceptions.
	 * 
	 * @param e
	 */
	protected void interrupted(final InterruptedException e) {
		Thread.currentThread().interrupt();
		ShapeDetector.handleException(e);
	}
}
