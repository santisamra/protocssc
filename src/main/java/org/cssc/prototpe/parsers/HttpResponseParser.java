package org.cssc.prototpe.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.parsers.exceptions.InvalidPacketException;

public class HttpResponseParser {

	private InputStream inputStream;

	public HttpResponseParser(InputStream inputStream) {
		this.inputStream = inputStream;
	}

	public HttpResponse parse() throws IOException {
		StringBuffer buffer = new StringBuffer();

		boolean firstLfRead = false;
		boolean secondLfRead = false;

		while(!(firstLfRead && secondLfRead)) {
			char readChar = (char)inputStream.read();
			
			if(readChar == '\n') {
				if(firstLfRead) {
					secondLfRead = true;
				} else {
					firstLfRead = true;
				}
			} else {
				firstLfRead = false;
				secondLfRead = false;
			}
			
			buffer.append(readChar);
		}

		String parsedString = buffer.toString();

		HttpResponseLexParser parser = new HttpResponseLexParser(new StringReader(parsedString));
		parser.parse();
		HttpResponse parsedResponse = parser.getParsedResponse();

		String transferCoding = parsedResponse.getHeader().getField("transfer-coding");

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

//			System.out.println("Read content length: " + contentLength);
			byte[] content = new byte[contentLength];

			inputStream.read(content);

			return new HttpResponse(
					parsedResponse.getVersion(),
					parsedResponse.getHeader(),
					parsedResponse.getStatusCode(),
					parsedResponse.getReasonPhrase(),
					content);

		} else if(transferCoding.equals("chunked")){
			System.out.println("Content is chunked!");
			/* I read the chunked-bodies. */
			int contentLength = 0;
			byte[] content = new byte[contentLength];
			
			/* I read the chunk size. */
			int chunkSize = readChunkSize();
			
			while(chunkSize != 0) {
				byte[] currentChunkData = new byte[chunkSize];
				inputStream.read(currentChunkData);
				
				char readChar = (char)inputStream.read();
				if(readChar != '\n') {
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
		boolean lfRead = false;
		StringBuffer buffer = new StringBuffer();
		
		//TODO: Mejorar el control de estas condiciones.
		while(!lfRead) {
			char readChar = (char)inputStream.read();
			
			if(readChar == '\n') {
				lfRead = true;
			} else {
				lfRead = false;
			}
			
			buffer.append(readChar);
		}
		
		try {
			System.out.println("Reading from buffer: " + buffer.toString().trim());
			return Integer.parseInt(buffer.toString().trim(), 16);
		} catch(NumberFormatException e) {
			throw new InvalidPacketException("Chunk size must be hexadecimal.");
		}
	}
	
}
