package helpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import exceptions.CAException;

/**
 * A list implementation of an n-dimensional array. Warning: only tested correct
 * for 2D.
 * 
 * @author Sean
 */
public class NDArrayList<V> implements Iterable<V> {
	protected final List<V> data;
	protected final int[] dimensions;
	protected final int roof;

	/**
	 * Creates an NDArrayList of n dimensions, of size (x_0, x_1, ..., x_n).
	 * <p>
	 * e.g. NDArrayList(5,9) creates a table with 5 rows and 9 columns.
	 * 
	 * @param n
	 */
	public NDArrayList(final int... x) {
		int roof = 1;
		for (int i = 0; i < x.length; i++) {
			roof *= x[i];
		}
		this.roof = roof;
		data = new ArrayList<V>(roof);
		for (int i = 0; i < roof; i++) {
			data.add(null);
		}
		dimensions = x;
	}

	/**
	 * Sets the element to the specified value.
	 * 
	 * @param value
	 * @param x
	 * @throws CAException
	 */
	public void set(final V value, final int... x) throws CAException {
		data.set(getIndex(x), value);
	}

	/**
	 * Gets the value of the element at the specified position.
	 * <p>
	 * Beware when storing the result in a primitive variable such as int, as
	 * the retrieved value may be null, causing a null exception.
	 * 
	 * @param value
	 * @param x
	 * @throws CAException
	 */
	public V get(final int... x) throws CAException {
		return data.get(getIndex(x));
	}

	protected int getIndex(final int... x) throws CAException {
		if (x.length != dimensions.length) {
			throw new CAException("Wrong number of dimensions, " + x.length
					+ " != " + dimensions.length);
		}

		int index = 0;
		for (int i = 0; i < x.length - 1; i++) {
			for (int j = i + 1; j < dimensions.length; j++) {
				index += x[i] * dimensions[j];
			}
		}
		index += x[x.length - 1];

		if (index < 0 || index >= roof) {
			throw new CAException("Out of bounds, index: " + index + ", size: "
					+ roof);
		}
		return index;
	}

	@Override
	public Iterator<V> iterator() {
		return data.iterator();
	}

	public int[] getDimensions() {
		return dimensions;
	}

	// public static void main(String[] args) throws CAException {
	// NDArrayList<Point2D> a = new NDArrayList<Point2D>(5, 5);
	// for (int i = 0; i < 5; i++) {
	// for (int j = 0; j < 5; j++) {
	// a.set(new Point2D.Double(i, j), i, j);
	// }
	// }
	//
	// for (int i = 0; i < 5; i++) {
	// for (int j = 0; j < 5; j++) {
	// Point2D point = a.get(i, j);
	// System.out.println(point);
	// }
	// }
	// }
}
