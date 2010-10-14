package org.cssc.prototpe.parsers.exceptions;

@SuppressWarnings("serial")
public class InvalidPacketException extends RuntimeException {

	public InvalidPacketException() {
	}

	public InvalidPacketException(String message) {
		super(message);
	}

	public InvalidPacketException(Throwable cause) {
		super(cause);
	}

	public InvalidPacketException(String message, Throwable cause) {
		super(message, cause);
	}

}
