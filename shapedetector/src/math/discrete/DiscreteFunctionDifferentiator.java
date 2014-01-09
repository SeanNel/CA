package math.discrete;

/**
 * Differentiates the (real) components of a discrete function.
 * 
 * @author Sean
 * 
 * @param <V>
 */
public class DiscreteFunctionDifferentiator<V extends Number> extends
		DiscreteFunction<V> {
	private DiscreteFunction<V> f;

	public DiscreteFunctionDifferentiator(int n, DiscreteFunction<V> f) {
		super(n);
		this.f = f;
	}

	@Override
	public double value(double x) {
		return f.value(x) - f.value(x + 1);
	}

}
