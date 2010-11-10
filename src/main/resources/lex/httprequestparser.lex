package org.cssc.prototpe.parsers.lex;

import org.cssc.prototpe.http.HttpMethod;
import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.parsers.exceptions.HttpParserException;
import java.io.IOException;
import java.io.StringReader;

@SuppressWarnings("all")

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
		HttpHeader header = new HttpHeader();
		
		if(remainingText != null && !"".equals(remainingText)) {
			HttpHeaderLexParser headerParser = new HttpHeaderLexParser(new StringReader(remainingText));
			headerParser.parse();
			header = headerParser.getParsedHeader();
		} else {
			header = new HttpHeader();
		}
		
		return new HttpRequest(version, header, path, method);
	}
%}


%eof{
%eof}

METHOD =	[A-Za-z]+
PATH =		\/.*|http:\/\/.*\/.*
VERSION =	HTTP\/
NEWLINE =	\r\n

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
	1\.[01][ ]?{NEWLINE} {
		version = yytext().trim();
		remainingText = "";
		yybegin(ADDING_REMAINING_TEXT);
	}
}

<ADDING_REMAINING_TEXT> {
	
	([^\r\n]+{NEWLINE})*{NEWLINE}	{
		remainingText = yytext().trim();
		return YYEOF; //PARCHEEE
	}
}

.|\n {
	throw new HttpParserException("Invalid packet.");
}