package org.cssc.prototpe.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.parsers.lex.HttpRequestLexParser;

public class HttpRequestParser extends HttpParser {
	
	public HttpRequestParser(InputStream inputStream) {
		super(inputStream);
	}
	
	public HttpRequest parse() throws IOException {
		String str = parseFirstPart();
		HttpRequestLexParser parser = new HttpRequestLexParser(new StringReader(str));
		parser.parse();

		parsedPacket = parser.getParsedRequest();
		return (HttpRequest)parsedPacket;
	}

}
