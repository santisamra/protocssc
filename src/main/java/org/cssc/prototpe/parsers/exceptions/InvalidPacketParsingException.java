package org.cssc.prototpe.parsers.exceptions;

@SuppressWarnings("serial")
public class InvalidPacketParsingException extends RuntimeException {

	public InvalidPacketParsingException() {
	}

	public InvalidPacketParsingException(String arg0) {
		super(arg0);
	}

	public InvalidPacketParsingException(Throwable arg0) {
		super(arg0);
	}

	public InvalidPacketParsingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
