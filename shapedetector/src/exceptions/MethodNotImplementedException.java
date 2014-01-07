package exceptions;

public class MethodNotImplementedException extends CAException {
	private static final long serialVersionUID = 1L;

	public MethodNotImplementedException() {
		super();
	}
	
	public MethodNotImplementedException(String message) {
		super(message);
	}

}
