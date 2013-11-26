package vectors;

public class PolarVector extends GenericVector {
	private static final long serialVersionUID = 1L;

	public PolarVector() {
		add(0.0);
		add(0.0);
	}

	public PolarVector(double e_r, double e_theta) {
		add(e_r);
		add(e_theta);
	}

	public CartesianVector cartesianVector() {
		double x = getE_r() * Math.cos(getE_theta());
		double y = getE_r() * Math.sin(getE_theta());
		return new CartesianVector(x, y);
	}

	public double getE_r() {
		return get(0);
	}

	public double getE_theta() {
		return get(1);
	}

	public void setE_r(double value) {
		set(0, value);
	}

	public void setE_theta(double value) {
		set(1, value);
	}

	public String toString() {
		return "PolarVector: E_r=" + getE_r() + ", E_theta=" + getE_theta();
	}

	// ------------------------------------------------------------------------
	// Vector operations.
	// ------------------------------------------------------------------------

	public PolarVector unitVector() {
		return new PolarVector(1.0, getE_theta());
	}

	//
	// public double absoluteValue() {
	// return Math.sqrt(getX() * getX() + getY() * getY());
	// }

	public PolarVector negative() {
		PolarVector vector = new PolarVector(getE_r(), getE_theta() + Math.PI);
		return vector;
	}

	// public PolarVector add(PolarVector vector2) {
	// PolarVector vector = new PolarVector();
	// vector.setE_r(getE_r() + vector2.getE_r());
	// vector.setE_theta(getE_theta() + vector2.getE_theta());
	// return vector;
	// }

	// public PolarVector add(CartesianVector vector2) {
	// PolarVector vector = vector2.polarVector();
	// vector.setE_r(getE_r() + vector.getE_r());
	// vector.setE_theta(getE_theta() + vector.getE_theta());
	// return vector;
	// }

	public PolarVector multiply(double factor) {
		PolarVector vector = new PolarVector(getE_r() * factor, getE_theta());
		return vector;
	}

	public double scalarProduct(PolarVector vector2) {
		return cartesianVector().scalarProduct(vector2.cartesianVector());
	}

	public boolean isZero() {
		return getE_r() == 0.0;
	}

	public PolarVector rotate(double theta) {
		PolarVector vector = new PolarVector(getE_r(), getE_theta() + theta);
		return vector.normalize();
	}

	public PolarVector normalize() {
		double r = getE_r();
		double theta = getE_theta();
		if (r < 0.0) {
			r = -r;
			theta += Math.PI;
		}
		while (theta > Math.PI * 2.0) {
			theta -= Math.PI * 2.0;
		}
		return new PolarVector(r, theta);
	}

	public PolarVector getPerpendicular() {
		return new PolarVector(1.0, getE_theta() + Math.PI / 2.0);
		// return cartesianVector().getPerpendicular().polarVector();
	}
	
	public double angle(PolarVector vector) {
		double angle = getE_theta() - vector.getE_theta();
		return normalize(angle);
	}

	public PolarVector reflect(PolarVector mirror) {
		PolarVector normal = mirror.getPerpendicular();
		double angleOfIncidence = angle(normal);
//		angleOfIncidence = angleOfIncidence + Math.PI;
//		 angleOfIncidence = getRepresentativeAngle(angleOfIncidence);
		// if (normalize(getE_theta()) < Math.PI) {
		// angleOfIncidence *= -1.0;
		// }
		PolarVector vector = rotate(angleOfIncidence);
		return vector;
	}

	protected double normalize(double theta) {
		while (theta < 0) {
			theta += 2.0 * Math.PI;
		}
		while (theta > 2.0 * Math.PI) {
			theta -= 2.0 * Math.PI;
		}
		return theta;
	}

	protected double getRepresentativeAngle(double theta) {
		while (theta < 0) {
			theta += Math.PI;
		}
		while (theta > Math.PI) {
			theta -= Math.PI;
		}
		return theta;
	}

	// getE_theta()

	public PolarVector flipX() {
		CartesianVector vector = this.cartesianVector();
		vector.setX(-vector.getX());
		return vector.polarVector();
	}

	public PolarVector flipY() {
		CartesianVector vector = this.cartesianVector();
		vector.setY(-vector.getY());
		return vector.polarVector();
	}
}
