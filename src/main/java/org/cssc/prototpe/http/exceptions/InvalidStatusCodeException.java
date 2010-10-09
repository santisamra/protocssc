package org.cssc.prototpe.http.exceptions;

@SuppressWarnings("serial")
public class InvalidStatusCodeException extends RuntimeException {

	public InvalidStatusCodeException() {
	}

	public InvalidStatusCodeException(String message) {
		super(message);
	}

	public InvalidStatusCodeException(Throwable cause) {
		super(cause);
	}

	public InvalidStatusCodeException(String message, Throwable cause) {
		super(message, cause);
	}

}
