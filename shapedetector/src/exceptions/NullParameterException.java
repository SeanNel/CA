package exceptions;

public class NullParameterException extends CAException {
	private static final long serialVersionUID = 1L;

	public NullParameterException(final String message) {
		super(message);
	}

}
