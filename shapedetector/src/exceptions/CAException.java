package exceptions;

public class CAException extends Exception {
	private static final long serialVersionUID = 1L;
	
	public CAException() {
		super();
	}
	
	public CAException(final String message) {
		super(message);
	}

}
