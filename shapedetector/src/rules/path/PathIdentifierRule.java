package rules.path;

import java.util.List;

import path.SDPath;

import rules.AbstractRule;
import shapes.AbstractShape;
import shapes.RootShape;

/**
 * Identifies paths as shapes.
 */
public class PathIdentifierRule<V> extends AbstractRule<SDPath> {
	protected final RootShape<V> shapeDetector = new RootShape<V>();
	protected final List<AbstractShape> shapeList;

	public PathIdentifierRule(final List<AbstractShape> shapeList) {
		super();
		this.shapeList = shapeList;
	}

	@Override
	public void update(final SDPath path) {
		if (path.getPerimeter() > 16) {
			shapeList.add(shapeDetector.identify(path));
		}
	}
}
