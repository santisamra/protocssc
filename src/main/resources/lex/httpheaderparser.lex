package org.cssc.prototpe.parsers;

import org.cssc.prototpe.http.HttpPacket;
import org.cssc.prototpe.parsers.exceptions.InvalidPacketParsingException;
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



FIELD_NAME =	[A-Za-z\-]+
FIELD_VALUE =	.+
NEWLINE = (\n|\r|\r\n|\n\r)

%state PARSING_VALUE

%%

<YYINITIAL> {
	[ ]?{FIELD_NAME}/:	{
		//System.out.println("Searching name.");
		currentName = yytext().trim();
		//System.out.println("Found name " + yytext().trim());
	}
	
	: {
		//System.out.println("Going to parsing value.");
		yybegin(PARSING_VALUE);
	}
}

<PARSING_VALUE> {
	[ ]?{FIELD_VALUE}[ ]?	{
		currentValue = yytext().trim();
		map.put(currentName, currentValue);
		//System.out.println("Found value " + yytext().trim());
	}
	
	{NEWLINE} {
		//System.out.println("Going to YYINITIAL.");
		yybegin(YYINITIAL);
	}
}

. {
	//System.out.println("Found error: \"" + yytext() + "\"");
	throw new InvalidPacketParsingException("Invalid package.");
}