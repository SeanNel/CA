package rules;

/** A rule that does nothing at all. */
public class DummyRule<V> extends AbstractRule<V> {

	public DummyRule() {
		super();
	}

	@Override
	public void update(final V obj) {
		/* Does nothing but demonstrate the overhead of applying a rule. */
	}
}
