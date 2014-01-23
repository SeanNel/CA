package ca.shapedetector;

import exceptions.CAException;
import exceptions.NullParameterException;
import helpers.Stopwatch;

import java.util.LinkedList;
import java.util.List;

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

	public ShapeList(final ShapeDetector sd) throws NullParameterException {
		if (sd == null) {
			throw new NullParameterException("sd");
		}
		this.sd = sd;
		shapes = new LinkedList<AbstractShape>();
		shapeRules = new LinkedList<ShapeRule>();

		// shapeRules.add(new ShapeDisplayRule(this, ShapeFrame.frame));
		shapeRules.add(new ShapeDrawRule(this, sd.getPicturePanel()));
	}

	/**
	 * Clears the list of shapes.
	 */
	public void clear() {
		shapes.clear();
	}

	/**
	 * Applies the shape rules to each shape in the list.
	 * 
	 * @throws CAException
	 */
	public void apply() throws CAException {
		System.out.println("Number of shapes: " + shapes.size());
		// System.out.println("Detected shapes: ");
		Stopwatch ruleStopwatch = new Stopwatch();

		for (ShapeRule rule : shapeRules) {
			rule.prepare();
			if (ShapeDetector.debug) {
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

			rule.complete();
			System.out.println(rule + ", elapsed time: " + ruleStopwatch.time()
					+ " ms");
		}
	}

	/**
	 * Adds the shape to the list.
	 * 
	 * @param shape
	 */
	public synchronized void addShape(final AbstractShape shape) {
		if (shape == null) {
			throw new RuntimeException();
		}
		shapes.add(shape);
	}
}
