package rules;

/**
 * Interface for classes that perform actions on objects.
 * 
 * @author Sean
 * 
 * @param <V>
 */
public interface Rule<V> {
	/**
	 * Does what has to be done before the rule starts.
	 */
	public void prepare() throws Exception;

	/**
	 * Applies the rule and updates the specified object.
	 * 
	 * @param object
	 * @throws Exception 
	 */
	public void update(final V object) throws Exception;

	/**
	 * Does what has to be done after the rule has completed.
	 */
	public void complete() throws Exception;
}
