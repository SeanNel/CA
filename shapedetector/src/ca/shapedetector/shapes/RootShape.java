package ca.shapedetector.shapes;

import java.util.ArrayList;
import java.util.List;

import ca.shapedetector.blob.Blob;
import ca.shapedetector.path.SDPath;
import exceptions.MethodNotImplementedException;

public class RootShape extends AbstractShape {
	protected final List<AbstractShape> relatedShapes;

	public RootShape() {
		super(null, null, 0d);
		relatedShapes = new ArrayList<AbstractShape>();
		relatedShapes.add(new Ellipse());
		relatedShapes.add(new Triangle());
		relatedShapes.add(new Quadrilateral());
	}

	public AbstractShape identify(Blob blob)
			throws MethodNotImplementedException {
		AbstractShape shape = new UnknownShape(new SDPath(blob));

		for (SDShape relatedShape : relatedShapes) {
			shape = relatedShape.identify(shape);
			if (!(shape instanceof UnknownShape)) {
				break;
			}
		}
		return shape;
	}

	@Override
	public AbstractShape identify(AbstractShape shape) {
		return null;
	}
}