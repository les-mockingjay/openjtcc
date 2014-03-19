package org.bytesoft.openjtcc;

public class NoSuchTransactionException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public NoSuchTransactionException() {
		super();
	}

	public NoSuchTransactionException(String message) {
		super(message);
	}

	public NoSuchTransactionException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoSuchTransactionException(Throwable cause) {
		super(cause);
	}

}
