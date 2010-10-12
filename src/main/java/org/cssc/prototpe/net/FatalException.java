package org.cssc.prototpe.net;

public class FatalException extends RuntimeException {

	private static final long serialVersionUID = 116770308264426388L;

	public FatalException() {
		super();
	}

	public FatalException(String message, Throwable cause) {
		super(message, cause);
	}

	public FatalException(String message) {
		super(message);
	}

	public FatalException(Throwable cause) {
		super(cause);
	}

}
