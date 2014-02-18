package rules;

public abstract class AbstractRule<V> implements Rule<V> {

	/**
	 * Constructor.
	 */
	public AbstractRule() {
	}

	@Override
	public void prepare() throws Exception {
	}

	@Override
	public void complete() throws Exception {
	}

	public String toString() {
		return this.getClass().getSimpleName();
	}
}
