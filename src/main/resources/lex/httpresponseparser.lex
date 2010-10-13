package org.cssc.prototpe.parsers;

import org.cssc.prototpe.http.HttpResponseCode;

%%
%class HttpResponseParser
%public
%function parse
%standalone

%{
	private HttpResponseCode responseCode;
	private String version;
	private String conent;
	
%}



METHOD =	[A-Za-z]+
PATH =		[A-Za-z0-9\-_\.\/\?=&:]+
VERSION =	HTTP\/
NEWLINE =	\r\n

%state PARSING_METHOD
%state PARSING_PATH
%state PARSING_VERSION
%state ADDING_REMAINING_TEXT

%%

a { }