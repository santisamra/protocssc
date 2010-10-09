package org.cssc.prototpe.http.exceptions;

@SuppressWarnings("serial")
public class InvalidMethodStringException extends RuntimeException {

	public InvalidMethodStringException() {
	}

	public InvalidMethodStringException(String arg0) {
		super(arg0);
	}

	public InvalidMethodStringException(Throwable arg0) {
		super(arg0);
	}

	public InvalidMethodStringException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
