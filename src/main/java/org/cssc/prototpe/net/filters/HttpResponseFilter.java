package org.cssc.prototpe.net.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.List;

import org.cssc.prototpe.http.HttpMethod;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.Logger;
import org.cssc.prototpe.parsers.HttpResponseParser;

public class HttpResponseFilter extends Filter {
	
	private static final boolean SQUID_PATCH = false;

	private static String MEDIA_TYPE_BLOCKED_HTML = "/html/errors/mediaTypeBlocked.html";
	private static String BIG_CONTENT_LENGTH_HTML = "/html/errors/bigContentLength.html";
	
	private Socket serverSocket;
	private HttpRequest request;
	private HttpResponse response;

	public HttpResponseFilter(Socket clientSocket, Socket serverSocket, HttpRequest request, HttpResponse response, Logger logger) {
		super(clientSocket, Application.getInstance().getApplicationConfiguration().getFilterForCondition(clientSocket.getInetAddress(), request.getHeader().getField("user-agent")), logger);
		this.request = request;
		this.response = response;
		this.serverSocket = serverSocket;
	}

	public boolean filter() throws IOException {
		if(filter == null) {
			return false;
		}

		List<String> blockedMediaTypes = filter.getBlockedMediaTypes();
		String contentTypeString = response.getHeader().getField("content-type");

		if(blockedMediaTypes != null && blockedMediaTypes.contains(contentTypeString)) {
			writeResponse(Application.class.getResourceAsStream(MEDIA_TYPE_BLOCKED_HTML));
			serverSocket.close();
			Application.getInstance().getMonitoringService().registerMediaTypeBlock();
			return true;
		}

		if(filter.getMaxContentLength() != -1) {
			String contentLengthString = response.getHeader().getField("content-length");

			if(contentLengthString != null) {
				int contentLength = Integer.valueOf(contentLengthString);
				int maxContentLength = filter.getMaxContentLength();

				if(contentLength > maxContentLength && maxContentLength != 0) {
					writeResponse(Application.class.getResourceAsStream(BIG_CONTENT_LENGTH_HTML));
					serverSocket.close();
					Application.getInstance().getMonitoringService().registerSizeBlock();
					return true;
				}
			}
		}

		return false;
	}

	@SuppressWarnings("all")
	public void filterAndWriteContent(HttpResponseParser parser, OutputStream outputStream) throws IOException {
		String contentTypeString = response.getHeader().getField("content-type");
		boolean hasContentLength = response.getHeader().containsField("content-length");
		boolean isContentEncoded = response.getHeader().containsField("content-encoding");
		boolean contentIsWritable = !request.getMethod().equals(HttpMethod.HEAD) && response.getStatusCode().isPossibleContent();

		if( !response.getStatusCode().isPossibleContent() && (!SQUID_PATCH || (!hasContentLength && !isContentEncoded))){
			response.getHeader().setField("content-length", "0");
			if(SQUID_PATCH) {
				hasContentLength = true;
			}
		}
		
		int maxContentLength = -1;
		boolean checkContentLength = false;
		boolean l33tTransform = false;
		boolean rotateImages = false;

		if(filter != null) {
			maxContentLength = filter.getMaxContentLength();
			checkContentLength = maxContentLength != -1 && hasContentLength;
			l33tTransform = filter.isL33tTransform() && isText(contentTypeString) && !isContentEncoded;
			rotateImages = filter.isRotateImages() && isImage(contentTypeString) && !isContentEncoded;
		}

		if(filter != null && contentIsWritable && (checkContentLength || l33tTransform || rotateImages)) {
			/* The response content has to be filtered. */

			String transferEncoding = response.getHeader().getField("transfer-encoding");

			byte[] content = new byte[0];
			int contentLength = 0;

			/* Content length is filtered. */
			if(checkContentLength && contentLength > maxContentLength) {
				writeResponse(Application.class.getResourceAsStream(BIG_CONTENT_LENGTH_HTML));
				serverSocket.close();
				Application.getInstance().getMonitoringService().registerSizeBlock();
				return;
			}

			if(transferEncoding != null) {
				if(transferEncoding.toLowerCase().equals("chunked")) {
					byte[] temp;

					while((temp = parser.readNextChunk(true)) != null) {
						contentLength += temp.length;

						/* Content length is filtered. */
						if(checkContentLength && contentLength > maxContentLength) {
							writeResponse(Application.class.getResourceAsStream(BIG_CONTENT_LENGTH_HTML));
							serverSocket.close();
							Application.getInstance().getMonitoringService().registerSizeBlock();
							return;
						}

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

					/* Content length is filtered. */
					if(checkContentLength && contentLength > maxContentLength) {
						writeResponse(Application.class.getResourceAsStream(BIG_CONTENT_LENGTH_HTML));
						serverSocket.close();
						Application.getInstance().getMonitoringService().registerSizeBlock();
						return;
					}

					byte[] aux = new byte[contentLength];

					System.arraycopy(content, 0, aux, 0, content.length);
					System.arraycopy(temp, 0, aux, content.length, readBytes);
					content = aux;
				}

			}

			byte[] transformed = null;

			if(l33tTransform) {
				transformed = TransformationUtilities.transforml33t(content);
				Application.getInstance().getMonitoringService().registerLeetTransformation();
			} else if(rotateImages) {
				transformed = TransformationUtilities.transform180Image(content);
				if(transformed == null) {
					transformed = content;
				} else {
					response.getHeader().setField("content-type", "image/png");
					Application.getInstance().getMonitoringService().registerImage180Transformation();
				}
			} else {
				transformed = content;
			}

			response.getHeader().setField("content-length", Integer.toString(transformed.length));
			response.getHeader().removeField("transfer-encoding");
			byte[] bytes = response.toString().getBytes(Charset.forName("US-ASCII"));
			outputStream.write(bytes);
			outputStream.write(transformed);

		} else {
			/* There are not filters to apply. */
			
			byte[] bytes = response.toString().getBytes(Charset.forName("US-ASCII"));
			outputStream.write(bytes);

			if(contentIsWritable) {

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
			}
		}
	}


	private boolean isImage(String contentTypeString) {
		return contentTypeString != null && (
				contentTypeString.equalsIgnoreCase("image/jpeg") ||
				contentTypeString.equalsIgnoreCase("image/png") ||
				contentTypeString.equalsIgnoreCase("image/gif"));
	}

	private boolean isText(String contentTypeString) {
		return contentTypeString != null && contentTypeString.toLowerCase().contains("text/plain");
	}
}
