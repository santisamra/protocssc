package org.cssc.prototpe.parsers;

import org.cssc.prototpe.http.HttpMethod;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.parsers.exceptions.InvalidPackageParsingException;
import java.io.IOException;
import java.io.StringReader;

%%
%class HttpRequestParser
%public
%function parse
%standalone

%{
	private String version;
	private String path;
	private HttpMethod method;
	private String remainingText;
	
	public HttpRequest getParsedRequest() throws IOException {
		parse();
		HttpRequest ret = new HttpRequest(version, path, method);
		
		if(remainingText != null && remainingText.length() > 0) {
			HttpHeaderParser headerParser = new HttpHeaderParser(new StringReader(remainingText));
			headerParser.parse();
			headerParser.fillHeader(ret);
		}
		
		return ret;
	}
%}


%eof{
/*
	System.out.println("Method: " + method);
	System.out.println("Path: " + path);
	System.out.println("Version: " + version);
	System.out.println("Remaining text:");
	System.out.println(remainingText);
*/
%eof}

METHOD =	[A-Za-z]+
PATH =		[A-Za-z0-9\-_\.\/\?=&]+
VERSION =	HTTP\/

%state PARSING_METHOD
%state PARSING_PATH
%state PARSING_VERSION
%state ADDING_REMAINING_TEXT

%%

<YYINITIAL> {
	[ ]?{METHOD}[ ] {
		method = HttpMethod.fromString(yytext().trim());
		yybegin(PARSING_PATH);
	}
}

<PARSING_PATH> {
	{PATH}[ ]/{VERSION} {
		path = yytext().trim();
	}
	
	{VERSION} {
		yybegin(PARSING_VERSION);
	}
}

<PARSING_VERSION> {
	1\.[01][ ]?/\n {
		version = yytext().trim();
		yybegin(ADDING_REMAINING_TEXT);
	}
}

<ADDING_REMAINING_TEXT> {
	(.|\n)*\n\n	{
		remainingText = yytext().trim();
	}
}

.|\n {
	throw new InvalidPackageParsingException("Invalid package.");
}