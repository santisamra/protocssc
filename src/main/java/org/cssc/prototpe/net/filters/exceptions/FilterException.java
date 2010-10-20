package org.cssc.prototpe.net.filters.exceptions;

@SuppressWarnings("serial")
public class FilterException extends RuntimeException {

	public FilterException() {
	}

	public FilterException(String arg0) {
		super(arg0);
	}

	public FilterException(Throwable arg0) {
		super(arg0);
	}

	public FilterException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
