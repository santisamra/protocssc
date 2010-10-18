package org.cssc.prototpe.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.parsers.lex.HttpResponseLexParser;

public class HttpResponseParser extends HttpParser {

	public HttpResponseParser(InputStream inputStream) {
		super(inputStream);
	}

	public HttpResponse parse() throws IOException {
		HttpResponseLexParser parser = new HttpResponseLexParser(new StringReader(parseFirstPart()));
		parser.parse();

		parsedPacket = parser.getParsedResponse();
		return (HttpResponse)parsedPacket;
	}

}
