package org.cssc.prototpe.configuration.exceptions;

@SuppressWarnings("serial")
public class ConfigurationParserException extends RuntimeException {

	public ConfigurationParserException() {
	}

	public ConfigurationParserException(String arg0) {
		super(arg0);
	}

	public ConfigurationParserException(Throwable arg0) {
		super(arg0);
	}

	public ConfigurationParserException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

}
