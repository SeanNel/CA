package ca.shapedetector;

import exceptions.CAException;
import helpers.Stopwatch;

import java.util.LinkedList;
import java.util.List;

import ca.Debug;
import ca.concurrency.ThreadServer;
import ca.rules.shape.ShapeDrawRule;
import ca.rules.shape.ShapeRule;
import ca.shapedetector.shapes.AbstractShape;

public class ShapeList {
	protected final ShapeDetector sd;
	/** Processes to apply to each SDShape in sequence. */
	protected final List<ShapeRule> shapeRules;
	/** List of detected shapes. */
	protected final List<AbstractShape> shapes;

	/*
	 * There is some concurrency issue with processing shapes in parallel. Set
	 * this to false to enable it anyway.
	 */
	private static boolean debug = true;

	public ShapeList(ShapeDetector sd) {
		this.sd = sd;
		shapes = new LinkedList<AbstractShape>();
		shapeRules = new LinkedList<ShapeRule>();

		// shapeRules.add(new ShapeDisplayRule(this, ShapeFrame.frame));
		shapeRules.add(new ShapeDrawRule(this, sd.getPicturePanel()));
	}

	public void clear() {
		shapes.clear();
	}

	public void update() throws CAException {
		System.out.println("Number of shapes: " + shapes.size());
		// System.out.println("Detected shapes: ");
		Stopwatch ruleStopwatch = new Stopwatch();

		for (ShapeRule rule : shapeRules) {
			rule.start();
			if (debug || Debug.debug) {
				/* Linear method */
				for (AbstractShape shape : shapes) {
					rule.update(shape);
				}
			} else {
				/* Multithreaded method */
				ThreadServer<AbstractShape> threadServer = new ThreadServer<AbstractShape>(
						rule, shapes, sd.getCA().getNumThreads());
				threadServer.run();
			}

			rule.end();
			System.out.println(rule + ", elapsed time: " + ruleStopwatch.time()
					+ " ms");
		}
	}

	public synchronized void addShape(AbstractShape shape) {
		if (shape == null) {
			throw new RuntimeException();
		}
		shapes.add(shape);
	}
}
