package org.cssc.prototpe.parsers;

import org.cssc.prototpe.http.HttpMethod;

%%
%class HttpRequestParser
%public
%function parse
%standalone

%{
	private String version;
	private String path;
	private HttpMethod method;
%}

%eof{
	System.out.println("Method: " + method);
	System.out.println("Path: " + path);
	System.out.println("Version: " + version);
%eof}

METHOD =	[A-Za-z]+
PATH =		[A-Za-z0-9\-_\.\/\?=&]+
VERSION =	HTTP\/

%state PARSING_METHOD
%state PARSING_PATH
%state PARSING_VERSION

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
	1\.[01] {
		version = yytext();
	}
}