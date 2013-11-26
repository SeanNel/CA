package vectors;

public class CartesianVector extends GenericVector {
	private static final long serialVersionUID = 1L;

	public CartesianVector() {
		add(0.0);
		add(0.0);
	}

	public CartesianVector(double x, double y) {
		add(x);
		add(y);
	}

	public PolarVector polarVector() {
		double e_r = absoluteValue();
		double e_theta = Math.atan2(getY(), getX());
		return new PolarVector(e_r, e_theta);
	}

	public double getX() {
		return get(0);
	}

	public double getY() {
		return get(1);
	}

	public int getIntX() {
		return (int) Math.round(get(0));
	}

	public int getIntY() {
		return (int) Math.round(get(1));
	}

	public void setX(double value) {
		set(0, value);
	}

	public void setY(double value) {
		set(1, value);
	}

	public String toString() {
		return "CartesianVector: x=" + getX() + ", y=" + getY();
	}

	// ------------------------------------------------------------------------
	// Vector operations.
	// ------------------------------------------------------------------------

	public CartesianVector unitVector() {
		double absoluteValue = absoluteValue();
		double x = 0.0, y = 0.0;
		if (absoluteValue != 0) {
			x = getX() / absoluteValue;
			y = getY() / absoluteValue;
		}
		return new CartesianVector(x, y);
	}

	public double absoluteValue() {
		return Math.sqrt(getX() * getX() + getY() * getY());
	}

	public CartesianVector negative() {
		CartesianVector vector = new CartesianVector();
		vector.setX(-getX());
		vector.setY(-getY());
		return vector;
	}

	public CartesianVector add(CartesianVector vector2) {
		CartesianVector vector = new CartesianVector();
		vector.setX(getX() + vector2.getX());
		vector.setY(getY() + vector2.getY());
		return vector;
	}

	public CartesianVector add(PolarVector vector2) {
		CartesianVector vector = vector2.cartesianVector();
		vector.setX(getX() + vector.getX());
		vector.setY(getY() + vector.getY());
		return vector;
	}

	public CartesianVector multiply(double factor) {
		CartesianVector vector = new CartesianVector();
		vector.setX(getX() * factor);
		vector.setY(getY() * factor);
		return vector;
	}

	public double scalarProduct(CartesianVector vector) {
		double x = getX() * vector.getX();
		double y = getY() * vector.getY();
		return x + y;
	}

	public boolean isZero() {
		return getX() == 0.0 && getY() == 0.0;
	}

	public CartesianVector rotate(double theta) {
		PolarVector vector = polarVector();
		vector = vector.rotate(theta);
		return vector.cartesianVector();
	}

	public CartesianVector getPerpendicular() {
		// A = a1*i + a2*j
		// B = b1*i + b2*j
		// => A.B = a1*b_1 + a2*b2 = 0
		// => a1 / a2 = - b2 / b1
		// So we choose an arbitrary value of b_1 and calculate b2, where
		// a1 = getX(), a2 = getY().
		CartesianVector vector = new CartesianVector(1.0, -getX() / getY());
		return vector.unitVector();

		// Alternatively, convert to polar and get perpendicular from there.
	}
}
