package org.cssc.prototpe.configuration.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.parsers.HttpResponseParser;

public class HttpResponseFilter extends Filter {

	private HttpResponse response;
	
	public HttpResponseFilter(Socket clientSocket, HttpRequest request, HttpResponse response) {
		super(clientSocket, Application.getInstance().getApplicationConfiguration().getFilterForCondition(clientSocket.getInetAddress(), request.getHeader().getField("user-agent")));
		this.response = response;
	}
	
	public boolean filter() throws IOException {
		if(filter == null) {
			return false;
		}
		
		List<String> blockedMediaTypes = filter.getBlockedMediaTypes();
		String contentTypeString = response.getHeader().getField("content-type");
		
		if(blockedMediaTypes != null && blockedMediaTypes.contains(contentTypeString)) {
			writeResponse("src/main/resources/html/errors/mediaTypeBlocked.html");
			return true;
		}
		
		String contentLengthString = response.getHeader().getField("content-length");
		
		if(contentLengthString != null) {
			double contentLength = Double.valueOf(contentLengthString);
			
			if(contentLength > filter.getMaxContentLength()) {
				writeResponse("src/main/resources/html/errors/bigContentLength.html");
				return true;
			}
		}
		
		return false;
	}
	
	public void filterAndWriteContent(HttpResponseParser parser, OutputStream outputStream) throws IOException {
		if(filter == null || (!filter.isL33tTransform() && !filter.isRotateImages())) {
			outputStream.write(response.toString().getBytes());
			String transferEncoding = response.getHeader().getField("transfer-encoding");

			if(transferEncoding != null) {
				if(transferEncoding.toLowerCase().equals("chunked")) {
					byte[] temp;

					while((temp = parser.readNextChunk()) != null) {
						outputStream.write(temp);
					}
				}

			} else {

				byte[] temp = new byte[1024];
				int readBytes;

				while((readBytes = parser.readNextNBodyBytes(temp, 0, 1024)) != -1) {
					outputStream.write(temp, 0, readBytes);
				}

			}
		} else {
			boolean l33tTransform = filter.isL33tTransform();
			boolean rotateImages = filter.isRotateImages();
			
			
		}
	}
}
