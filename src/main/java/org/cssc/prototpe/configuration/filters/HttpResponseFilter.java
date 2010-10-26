package org.cssc.prototpe.configuration.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.List;
import java.util.zip.GZIPOutputStream;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.parsers.HttpResponseParser;
import org.cssc.prototpe.transformations.TransformationUtilities;

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
			double maxContentLength = filter.getMaxContentLength();

			if(contentLength > maxContentLength && maxContentLength != 0) {
				writeResponse("src/main/resources/html/errors/bigContentLength.html");
				return true;
			}
		}

		return false;
	}

	public void filterAndWriteContent(HttpResponseParser parser, OutputStream outputStream) throws IOException {
		System.out.println("Here.");
		System.out.println("Filter: " + filter);
		System.out.println("Has not to be filtered: " + !hasToBeFiltered(response));
		System.out.println("L33t: " + filter.isL33tTransform());
		System.out.println("Rotate: " + filter.isRotateImages());

		if(filter == null || !hasToBeFiltered(response) || (!filter.isL33tTransform() && !filter.isRotateImages())) {
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
				
				System.out.println("I have to filter.");

				byte[] temp = new byte[1024];
				int readBytes;

				while((readBytes = parser.readNextNBodyBytes(temp, 0, 1024)) != -1) {
					outputStream.write(temp, 0, readBytes);
				}

			}
		} else {
			boolean l33tTransform = filter.isL33tTransform();
			boolean rotateImages = filter.isRotateImages();
			
//			outputStream.write(response.toString().getBytes());
			String transferEncoding = response.getHeader().getField("transfer-encoding");

			byte[] content = new byte[0];
			int contentLength = 0;

			if(transferEncoding != null) {
				if(transferEncoding.toLowerCase().equals("chunked")) {
					byte[] temp;

					while((temp = parser.readNextChunk()) != null) {
						contentLength += temp.length;
						byte[] aux = new byte[contentLength];

						System.arraycopy(content, 0, aux, 0, content.length);
						System.arraycopy(temp, 0, aux, content.length, temp.length);
						content = aux;
					}
				}

			} else {

				byte[] temp = new byte[1024];
				int readBytes;

				while((readBytes = parser.readNextNBodyBytes(temp, 0, 1024)) != -1) {
					contentLength += readBytes;
					byte[] aux = new byte[contentLength];

					System.arraycopy(content, 0, aux, 0, content.length);
					System.arraycopy(temp, 0, aux, content.length, readBytes);
					content = aux;
				}

			}
			
			String contentTypeString = response.getHeader().getField("content-type");

			if(l33tTransform && isText(contentTypeString)) {
				byte[] transformed = TransformationUtilities.transforml33t(content);
				response.getHeader().setField("content-length", Integer.toString(transformed.length));
				response.getHeader().removeField("content-encoding");
				response.getHeader().removeField("transfer-encoding");
				outputStream.write(response.toString().getBytes());
				outputStream.write(transformed);
			}
		}
	}

	private boolean hasToBeFiltered(HttpResponse response) {
		String contentTypeString = response.getHeader().getField("content-type");
		
		return !response.getHeader().containsField("content-encoding") && (isImage(contentTypeString) || isText(contentTypeString));
	}
	
	private boolean isImage(String contentTypeString) {
		return contentTypeString != null && (
				contentTypeString.equalsIgnoreCase("image/jpeg") ||
				contentTypeString.equalsIgnoreCase("image/png") ||
				contentTypeString.equalsIgnoreCase("image/gif"));
	}
	
	private boolean isText(String contentTypeString) {
		return contentTypeString != null && contentTypeString.equalsIgnoreCase("text/plain");
	}
	
	private void print(byte[] buffer){
		System.out.print("\"");
		for( int i = 0; i < buffer.length; i++){
			System.out.print((char)buffer[i]);
		}
		System.out.print("\"");
	}
}
