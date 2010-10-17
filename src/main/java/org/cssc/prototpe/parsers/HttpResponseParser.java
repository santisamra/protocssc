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
			int readInt = -1;
			while(readInt == -1) {
				readInt = inputStream.read();
			}
			char readChar = (char)readInt;
			
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
				throw new InvalidPacketException("The packet has not a transfer encoding nor a content length.");
			}

			int contentLength;

			try {
				contentLength = Integer.valueOf(contentLengthString);
			} catch(NumberFormatException e) {
				throw new InvalidPacketException("Invalid content length.");
			}

			byte[] content = new byte[contentLength];

			int offset = 0;
			while(offset < contentLength) {
				offset += inputStream.read(content, offset, contentLength - offset);
			}

			return new HttpResponse(
					parsedResponse.getVersion(),
					parsedResponse.getHeader(),
					parsedResponse.getStatusCode(),
					parsedResponse.getReasonPhrase(),
					content);

		} else if(transferCoding.toLowerCase().equals("chunked")){
			/* I read the chunked-bodies. */
			int contentLength = 0;
			byte[] content = new byte[contentLength];
			
			/* I read the chunk size. */
			int chunkSize = readChunkSize();
			
			while(chunkSize != 0) {
				byte[] currentChunkData = new byte[chunkSize];
				int offset = 0;
				while(offset < chunkSize) {
					offset += inputStream.read(currentChunkData, offset, chunkSize - offset);
				}
				
				int cr = inputStream.read();
				int lf = inputStream.read();
//				System.out.println(cr);
//				System.out.println(lf);
				if(cr != 13 || lf != 10) {
					throw new InvalidPacketException("Invalid chunked data.");
				}
				
				/* I append the arrays. */
				byte[] temp = new byte[contentLength + chunkSize];
				System.arraycopy(content, 0, temp, 0, contentLength);
				System.arraycopy(currentChunkData, 0, temp, contentLength, chunkSize);
				content = temp;
				
				contentLength += chunkSize;
				chunkSize = readChunkSize();
			}
			
			/* The conent is not chunked now. */
			parsedResponse.getHeader().removeField("transfer-encoding");
			parsedResponse.getHeader().setField("content-length", Integer.toString(contentLength));
			
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
			int readInt = -1;
			while(readInt == -1) {
				readInt = inputStream.read();
			}
			char readChar = (char)readInt;
			
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
