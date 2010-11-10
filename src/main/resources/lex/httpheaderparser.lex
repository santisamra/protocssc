package org.cssc.prototpe.parsers.lex;

import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.parsers.exceptions.HttpParserException;

@SuppressWarnings("all")

%%
%class HttpHeaderLexParser
%public
%function parse
%standalone

%{
	private HttpHeader header;
	private String currentName;
	private String currentValue;
	
	public HttpHeader getParsedHeader() {
		return header;
	}
%}

%init{
	header = new HttpHeader();
%init}



FIELD_NAME =	[A-Za-z0-9\-_]+
FIELD_VALUE =	[^\r\n]+
NEWLINE = \r\n

%state PARSING_VALUE

%%

<YYINITIAL> {
	[ ]?{FIELD_NAME}/:	{
		currentName = yytext().toLowerCase().trim();
	}
	
	: {
		yybegin(PARSING_VALUE);
	}
}

<PARSING_VALUE> {
	[ ]?{FIELD_VALUE}[ ]?	{
		currentValue = yytext().trim();
		header.setField(currentName, currentValue);
	}
	
	{NEWLINE} {
		yybegin(YYINITIAL);
	}
}

. {
	throw new HttpParserException("Invalid package.");
}