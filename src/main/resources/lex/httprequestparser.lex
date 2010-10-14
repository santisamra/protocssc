package org.cssc.prototpe.parsers;

import org.cssc.prototpe.http.HttpMethod;
import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.parsers.exceptions.InvalidPacketParsingException;
import java.io.IOException;
import java.io.StringReader;

%%
%class HttpRequestLexParser
%public
%function parse
%standalone

%{
	private String version;
	private String path;
	private HttpMethod method;
	private String remainingText;
	
	public HttpRequest getParsedRequest() throws IOException {
		HttpRequest ret;
		HttpHeader header;
		
		if(remainingText != null && remainingText.length() > 0) {
			HttpHeaderParser headerParser = new HttpHeaderParser(new StringReader(remainingText));
			headerParser.parse();
			header = headerParser.getParsedHeader();
		} else {
			header = new HttpHeader();
		}
		
		return new HttpRequest(version, header, path, method);
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
PATH =		[A-Za-z0-9\-_\.\/\?=&:]+
VERSION =	HTTP\/
NEWLINE =	\r\n

%state PARSING_METHOD
%state PARSING_PATH
%state PARSING_VERSION
%state ADDING_REMAINING_TEXT

%%

<YYINITIAL> {
	[ ]?{METHOD}[ ] {
		System.out.println("Found method: " + yytext().trim());
		method = HttpMethod.fromString(yytext().trim());
		yybegin(PARSING_PATH);
	}
}

<PARSING_PATH> {
	{PATH}[ ]/{VERSION} {
		path = yytext().trim();
		System.out.println("Found path: " + yytext().trim());
	}
	
	{VERSION} {
		yybegin(PARSING_VERSION);
	}
}

<PARSING_VERSION> {
	1\.[01][ ]?{NEWLINE} {
		version = yytext().trim();
		System.out.println("I got here");
		remainingText = "";
		yybegin(ADDING_REMAINING_TEXT);
	}
}

<ADDING_REMAINING_TEXT> {
	
	([^\r\n]+{NEWLINE})*{NEWLINE}	{
		System.out.println("here, finishing parsing");
		remainingText = yytext().trim();
		return YYEOF; //PARCHEEE
	}
}

.|{NEWLINE} {
	System.out.println("ERROR");
	System.out.println("Method: " + method);
	System.out.println("Path: " + path);
	System.out.println("Version: " + version);
	throw new InvalidPacketParsingException("Invalid packet.");
}