package org.cssc.prototpe.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.parsers.exceptions.InvalidPacketException;
import org.cssc.prototpe.parsers.lex.HttpResponseLexParser;

public class HttpResponseParser {

	private InputStream inputStream;

	public HttpResponseParser(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public HttpResponse parse() throws IOException {
		StringBuffer buffer = new StringBuffer();

		boolean firstCrRead = false;
		boolean firstLfRead = false;
		boolean secondCrRead = false;
		boolean secondLfRead = false;

		while(!(firstCrRead && firstLfRead &&
				secondCrRead && secondLfRead)) {
			char readChar = (char)inputStream.read();
			
			if(readChar == '\r') {
				if(firstCrRead && firstLfRead) {
					secondCrRead = true;
				} else {
					firstCrRead = true;
					firstLfRead = false;
					secondCrRead = false;
					secondLfRead = false;
				}
			} else if(readChar == '\n') {
				if(firstCrRead && !secondCrRead) {
					firstLfRead = true;
				} else if(secondCrRead) {
					secondLfRead = true;
				}
			} else {
				firstCrRead = false;
				firstLfRead = false;
				secondCrRead = false;
				secondLfRead = false;
			}
			
			buffer.append(readChar);
		}

		String parsedString = buffer.toString();

		HttpResponseLexParser parser = new HttpResponseLexParser(new StringReader(parsedString));
		parser.parse();
		HttpResponse parsedResponse = parser.getParsedResponse();

		String transferCoding = parsedResponse.getHeader().getField("transfer-encoding");

		if(transferCoding == null) {
			String contentLengthString = parsedResponse.getHeader().getField("content-length");

			if(contentLengthString == null) {
				throw new InvalidPacketException("The packet has not a transfer enconding nor a content length.");
			}

			int contentLength;

			try {
				contentLength = Integer.valueOf(contentLengthString);
			} catch(NumberFormatException e) {
				throw new InvalidPacketException("Invalid content length.");
			}

			byte[] content = new byte[contentLength];

			inputStream.read(content);

			return new HttpResponse(
					parsedResponse.getVersion(),
					parsedResponse.getHeader(),
					parsedResponse.getStatusCode(),
					parsedResponse.getReasonPhrase(),
					content);

		} else if(transferCoding.equals("chunked")){
			/* I read the chunked-bodies. */
			int contentLength = 0;
			byte[] content = new byte[contentLength];
			
			/* I read the chunk size. */
			int chunkSize = readChunkSize();
			
			System.out.println("Total = " + contentLength + "; this = " + chunkSize);
			
			while(chunkSize != 0) {
				byte[] currentChunkData = new byte[chunkSize];
				inputStream.read(currentChunkData);
				
				char cr = (char)inputStream.read();
				char lf = (char)inputStream.read();
				if(cr != '\r' || lf != '\n') {
					throw new InvalidPacketException("Invalid chunked data.");
				}
				
				/* I append the arrays. */
				byte[] temp = new byte[contentLength + chunkSize];
				System.arraycopy(content, 0, temp, 0, contentLength);
				System.arraycopy(currentChunkData, 0, temp, contentLength, chunkSize);
				content = temp;
				
				contentLength += chunkSize;
				chunkSize = readChunkSize();
				
				System.out.println("Total = " + contentLength + "; this = " + chunkSize);
			}
			
			return new HttpResponse(
					parsedResponse.getVersion(),
					parsedResponse.getHeader(),
					parsedResponse.getStatusCode(),
					parsedResponse.getReasonPhrase(),
					content);
		}
		
		return null;
	}
	
	private int readChunkSize() throws IOException {
		boolean crRead = false;
		boolean lfRead = false;
		StringBuffer buffer = new StringBuffer();
		
		while(!(crRead && lfRead)) {
			char readChar = (char)inputStream.read();
			
			if(readChar == '\r') {
				crRead = true;
			} else if(readChar == '\n') {
				if(crRead) {
					lfRead = true;
				} else {
					crRead = false;
					lfRead = false;
				}
			} else {
				crRead = false;
				lfRead = false;
			}
			
			buffer.append(readChar);
		}
		
		try {
			return Integer.parseInt(buffer.toString().trim(), 16);
		} catch(NumberFormatException e) {
			throw new InvalidPacketException("Chunk size must be hexadecimal.");
		}
	}
	
}
