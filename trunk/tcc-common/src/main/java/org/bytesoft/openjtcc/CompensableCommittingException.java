package org.bytesoft.openjtcc;

public class CompensableCommittingException extends CompensableException {
	private static final long serialVersionUID = 1L;

	public CompensableCommittingException() {
		super();
	}

	public CompensableCommittingException(String message) {
		super(message);
	}

	public CompensableCommittingException(String message, Throwable cause) {
		super(message, cause);
	}

	public CompensableCommittingException(Throwable cause) {
		super(cause);
	}

}
