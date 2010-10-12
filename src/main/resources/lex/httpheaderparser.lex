package org.cssc.prototpe.parsers;

import org.cssc.prototpe.http.HttpPacket;
import org.cssc.prototpe.parsers.exceptions.InvalidPackageParsingException;
import java.util.HashMap;
import java.util.Map;

%%
%class HttpHeaderParser
%public
%function parse
%standalone

%{
	private Map<String, String> map;
	private String currentName;
	private String currentValue;
	
	public void fillHeader(HttpPacket packet) {
		for(String key: map.keySet()) {
			packet.getHeader().setField(key, map.get(key));
		}
	}
%}

%init{
	map = new HashMap<String, String>();
%init}



FIELD_NAME =	[A-Za-z]+
FIELD_VALUE =	[A-Za-z0-9\-_\.]+

%state PARSING_VALUE

%%

<YYINITIAL> {
	[ ]?{FIELD_NAME}/:	{
		currentName = yytext().trim();
		System.out.println("Found name " + yytext().trim());
	}
	
	: {
		System.out.println("Going to parsing value.");
		yybegin(PARSING_VALUE);
	}
}

<PARSING_VALUE> {
	[ ]?{FIELD_VALUE}[ ]?	{
		currentValue = yytext().trim();
		map.put(currentName, currentValue);
		System.out.println("Found value " + yytext().trim());
	}
	
	\n {
		yybegin(YYINITIAL);
	}
}

. {
	System.out.println("Found: \"" + yytext() + "\"");
	throw new InvalidPackageParsingException("Invalid package.");
}