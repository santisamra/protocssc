package org.cssc.prototpe.parsers.exceptions;

@SuppressWarnings("serial")
public class HttpParserException extends RuntimeException {

	public HttpParserException() {
	}

	public HttpParserException(String arg0) {
		super(arg0);
	}

	public HttpParserException(Throwable arg0) {
		super(arg0);
	}

	public HttpParserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}
}
