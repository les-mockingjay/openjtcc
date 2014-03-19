package org.bytesoft.openjtcc;

/**
 * @author yangming.liu
 */
public class CompensableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public CompensableException() {
		super();
	}

	public CompensableException(String message) {
		super(message);
	}

	public CompensableException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompensableException(Throwable cause) {
		super(cause);
	}

}
