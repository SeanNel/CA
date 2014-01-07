package ca.shapedetector;

import exceptions.CAException;
import graphics.SDPanel;
import helpers.Stopwatch;

import java.util.LinkedList;
import java.util.List;

import ca.rules.shape.ShapeDrawRule;
import ca.rules.shape.ShapeRule;
import ca.shapedetector.shapes.SDShape;

public class ShapeList {
	/** List of detected shapes. */
	protected List<SDShape> shapes;
	/** Processes to apply to each SDShape in sequence. */
	public List<ShapeRule> shapeRules;

	public void load(ShapeDetector ca) {
		shapes = new LinkedList<SDShape>();
		loadRules(ca);
	}

	protected void loadRules(ShapeDetector ca) {
		shapeRules = new LinkedList<ShapeRule>();
		// shapeRules.add(new ShapeDisplayRule(this, ShapeFrame.frame));
		shapeRules.add(new ShapeDrawRule(ca.getShapeList(), (SDPanel) ca
				.getPicturePanel()));
	}

	public void update() throws CAException {
		System.out.println("Number of shapes: " + shapes.size());
		// System.out.println("Detected shapes: ");
		Stopwatch ruleStopwatch = new Stopwatch();

		for (ShapeRule rule : shapeRules) {
			rule.start();
			/* Linear method */
			for (SDShape shape : shapes) {
				rule.update(shape);
			}
			/*
			 * TODO: fix bugs when using the multithreaded method here...
			 */
			/* Multithreaded method */
			// ThreadServer<SDShape> threadServer = new ThreadServer<SDShape>(
			// rule, shapes);
			// threadServer.run();

			rule.end();
			System.out.println(rule + ", elapsed time: " + ruleStopwatch.time()
					+ " ms");
		}
	}

	public void addShape(SDShape shape) {
		shapes.add(shape);
	}
}
