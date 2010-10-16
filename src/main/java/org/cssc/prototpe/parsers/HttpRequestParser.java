package org.cssc.prototpe.parsers;

import java.io.IOException;
import java.io.InputStream;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.parsers.lex.HttpRequestLexParser;

public class HttpRequestParser {
	
	private InputStream inputStream;
	
	public HttpRequestParser(InputStream inputStream) {
		this.inputStream = inputStream;
	}
	
	public HttpRequest parse() throws IOException {
		HttpRequestLexParser parser = new HttpRequestLexParser(inputStream);
		parser.parse();
		return parser.getParsedRequest();
	}

}
