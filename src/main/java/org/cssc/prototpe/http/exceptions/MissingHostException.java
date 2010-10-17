package org.cssc.prototpe.http.exceptions;

public class MissingHostException extends Exception {

	private static final long serialVersionUID = -6826303609036067859L;

	public MissingHostException() {
		super();
	}

	public MissingHostException(String message, Throwable cause) {
		super(message, cause);
	}

	public MissingHostException(String message) {
		super(message);
	}

	public MissingHostException(Throwable cause) {
		super(cause);
	}

}
