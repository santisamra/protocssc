package org.cssc.prototpe.parsers.lex;

import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.parsers.exceptions.InvalidPacketParsingException;
import java.io.IOException;
import java.io.StringReader;
import java.io.Reader;
import java.io.InputStream;

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
			HttpHeaderParser headerParser = new HttpHeaderParser(new StringReader(remainingText));
			headerParser.parse();
			header = headerParser.getParsedHeader();
		} else {
			header = new HttpHeader();
		}
		
		return new HttpResponse(version, header, statusCode, reasonPhrase, null);
	}
	
%}

%eof{

	//System.out.println("Version: " + version);
	//System.out.println("Status code: " + statusCode);
	//System.out.println("Reason phrase: " + reasonPhrase);
	//System.out.println("Remaining text: \"");
	//System.out.println(remainingText + "\"");

%eof}


VERSION =		HTTP\/
STATUS_CODE =	[1-5][0-9]{2}
REASON_PHRASE =	[A-Za-z ]+
NEWLINE =		\r\n

%state PARSING_VERSION
%state PARSING_STATUS_CODE
%state PARSING_REASON_PHRASE
%state ADDING_REMAINING_TEXT
%state PARSING_CONTENT

%%

<YYINITIAL> {
	[ ]?{VERSION} {
		//System.out.println("Encontre version.");
		yybegin(PARSING_VERSION);
	}
}

<PARSING_VERSION> {
	1\.[01][ ] {
		//System.out.println("Encontre numero de version.");
		version = yytext().trim();
		yybegin(PARSING_STATUS_CODE);
	}
}

<PARSING_STATUS_CODE> {
	{STATUS_CODE}[ ] {
		//System.out.println("Encontre status code.");
		statusCode = HttpResponseCode.fromInt(Integer.valueOf(yytext().trim()));
		yybegin(PARSING_REASON_PHRASE);
	}
}

<PARSING_REASON_PHRASE> {
	{REASON_PHRASE}[ ]?{NEWLINE} {
		//System.out.println("Encontre reason phrase.");
		reasonPhrase = yytext().trim();
		yybegin(ADDING_REMAINING_TEXT);
	}
}

<ADDING_REMAINING_TEXT> {
	([^\r\n]+{NEWLINE})*{NEWLINE} {
		//System.out.println("Encontre remaining text.");
		remainingText = yytext().trim();
	}
}

(.|{NEWLINE}) {
	System.out.println("----- ERROR PARSING RESPONSE -----");
	System.out.println("Version: " + version);
	System.out.println("Status code: " + statusCode);
	System.out.println("Reason phrase: " + reasonPhrase);
	System.out.println("Remaining text:");
	System.out.println(remainingText);
	throw new InvalidPacketParsingException("Invalid packet.");
}