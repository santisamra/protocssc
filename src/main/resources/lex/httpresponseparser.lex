package org.cssc.prototpe.parsers.lex;

import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.parsers.exceptions.HttpParserException;
import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;
import java.io.InputStream;

@SuppressWarnings("all")

%%
%class HttpResponseLexParser
%public
%function parse
%standalone

%{
	private String version;
	private HttpResponseCode statusCode;
	private String reasonPhrase;
	private String remainingText;	
	
	public HttpResponse getParsedResponse() throws IOException {
		HttpResponse ret;
		HttpHeader header;
		
		if(remainingText != null && remainingText.length() > 0) {
			HttpHeaderLexParser headerParser = new HttpHeaderLexParser(new StringReader(remainingText));
			headerParser.parse();
			header = headerParser.getParsedHeader();
		} else {
			header = new HttpHeader();
		}
		
		return new HttpResponse(version, header, statusCode, reasonPhrase);
	}
	
%}

%eof{

%eof}


VERSION =		HTTP\/
STATUS_CODE =	[1-5][0-9]{2}
REASON_PHRASE =	[A-Za-z _-]+
NEWLINE =		\r\n

%state PARSING_VERSION
%state PARSING_STATUS_CODE
%state PARSING_REASON_PHRASE
%state ADDING_REMAINING_TEXT
%state PARSING_CONTENT

%%

<YYINITIAL> {
	[ ]?{VERSION} {
		yybegin(PARSING_VERSION);
		
	}
}

<PARSING_VERSION> {
	1\.[01][ ] {
		version = yytext().trim();
		yybegin(PARSING_STATUS_CODE);
	}
}

<PARSING_STATUS_CODE> {
	{STATUS_CODE}[ ] {
		statusCode = HttpResponseCode.fromInt(Integer.valueOf(yytext().trim()));
		yybegin(PARSING_REASON_PHRASE);
	}
}


<PARSING_REASON_PHRASE> {
	{NEWLINE} {
		yybegin(ADDING_REMAINING_TEXT);
	}

	{REASON_PHRASE}[ ]?{NEWLINE} {
		reasonPhrase = yytext().trim();
		yybegin(ADDING_REMAINING_TEXT);
	}
}

<ADDING_REMAINING_TEXT> {
	([^\r\n]+{NEWLINE})*{NEWLINE} {
		remainingText = yytext().trim();
	}
}

(.|\n) {
	throw new HttpParserException("Invalid packet.");
}