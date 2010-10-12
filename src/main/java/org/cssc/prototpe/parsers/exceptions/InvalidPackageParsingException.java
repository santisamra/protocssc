package org.cssc.prototpe.parsers.exceptions;

@SuppressWarnings("serial")
public class InvalidPackageParsingException extends RuntimeException {

	public InvalidPackageParsingException() {
	}

	public InvalidPackageParsingException(String arg0) {
		super(arg0);
	}

	public InvalidPackageParsingException(Throwable arg0) {
		super(arg0);
	}

	public InvalidPackageParsingException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
