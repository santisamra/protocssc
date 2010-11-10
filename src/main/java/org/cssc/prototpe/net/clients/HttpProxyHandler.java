package org.cssc.prototpe.net.clients;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.cssc.prototpe.http.HttpMethod;
import org.cssc.prototpe.http.HttpPacket;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.http.exceptions.InvalidMethodStringException;
import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;
import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.ApplicationConfiguration;
import org.cssc.prototpe.net.Logger;
import org.cssc.prototpe.net.filters.HttpRequestFilter;
import org.cssc.prototpe.net.filters.HttpResponseFilter;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.net.interfaces.ServerManager;
import org.cssc.prototpe.net.monitoring.MonitoredInputStream;
import org.cssc.prototpe.net.monitoring.MonitoredOutputStream;
import org.cssc.prototpe.parsers.HttpParser;
import org.cssc.prototpe.parsers.HttpRequestParser;
import org.cssc.prototpe.parsers.HttpResponseParser;
import org.cssc.prototpe.parsers.exceptions.HttpParserException;

public class HttpProxyHandler implements ClientHandler{

	private Socket clientSocket;
	private Socket serverSocket;
	private HttpRequestParser requestParser;
	private HttpResponseParser responseParser;
	private HttpRequest request;
	private HttpResponse response;
	private ApplicationConfiguration configuration;

	private Logger logger;
	private ServerManager serverManager;

	public HttpProxyHandler() {
		this.logger = Application.getInstance().getLogger();
		this.serverManager = Application.getInstance().getServerManager();
		this.configuration = Application.getInstance().getApplicationConfiguration();
	}


	@Override
	public void handle(Socket socket) {
		this.clientSocket = socket;

		boolean closedConnection = false;

		try {
			this.clientSocket.setSoTimeout(configuration.getClientKeepAliveTimeout());
		} catch(SocketException e) {
			closeClientSocket();
			return;
		}
		while(!closedConnection) {
			serverSocket = null;
			response = null;
			try {

				// Reading request.
				try {
					listenAndParseRequest();
				} catch(HttpParserException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST);
					sendErrorResponse();
					return;
				} catch(InvalidMethodStringException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.NOT_IMPLEMENTED);
					sendErrorResponse();
					return;
				} catch(SocketTimeoutException e) {
					closeClientSocket();
					return;
				}

				HttpRequestFilter requestFilter = new HttpRequestFilter(clientSocket, request, logger);

				// Generating connection and filtering request.
				try {
					if(requestFilter.filter()) {
						closeClientSocket();
						return;
					}
					
					generateServerSocket();

				} catch (MissingHostException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST);
					sendErrorResponse();
					return;
				} catch (UnknownHostException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.GATEWAY_TIMEOUT);
					sendErrorResponse();
					return;
				}

				try {
					// Writing request to origin server.
					System.out.println("About to write request:");
					System.out.println("To host: " + request.getEffectiveHost());
					System.out.println("Path: " + request.getEffectivePath());
					System.out.println("");
					writeHttpPacket(request, requestParser, new MonitoredOutputStream(serverSocket.getOutputStream(), false), false, false);
					try {
						// Reading response from origin server.
						listenAndParseResponse();
					} catch(InvalidStatusCodeException e) {
						// Invalid status code for the HTTP response.
						e.printStackTrace();
						response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
						sendErrorResponse();
						return;
					} catch(HttpParserException e) {
						// Invalid response format.
						e.printStackTrace();
						response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
						sendErrorResponse();
						return;
					} catch(IOException e) {
						// If post, do not retry; we sent the message successfully
						if(request.getMethod().equals(HttpMethod.POST)) {
							e.printStackTrace();
							response = HttpResponse.emptyResponse(HttpResponseCode.INTERNAL_SERVER_ERROR);
							sendErrorResponse();
							return;
						}
						throw e;
					}
				} catch(IOException e2) {
					// Unidentified error when writing request/reading response
					// Must retry only once, with a new socket
					try {
						serverSocket.close();
						generateServerSocket(true); // EMERGENCY SOCKET: always unused before
						// Rewriting request to the origin server.
						System.out.println("About to retry writing request:");
						System.out.println("To host: " + request.getEffectiveHost());
						System.out.println("Path: " + request.getEffectivePath());
						System.out.println("");
						
						writeHttpPacket(request, requestParser, new MonitoredOutputStream(serverSocket.getOutputStream(), false), false, false);
						try {
							// Reading response from origin server.
							listenAndParseResponse();
						} catch(InvalidStatusCodeException e) {
							// Invalid status code for the HTTP response.
							e.printStackTrace();
							response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
							sendErrorResponse();
							return;
						} catch(HttpParserException e) {
							// Invalid response format.
							e.printStackTrace();
							response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
							sendErrorResponse();
							return;
						}
					} catch(Exception e1) {
						// Unidentified error when trying to write request or read response from server.
						e1.printStackTrace();
						response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
						sendErrorResponse();
						return;
					}
				}



				boolean mustCloseServerConnection = response.mustCloseConnection();
				response.getHeader().removeField("connection");
				if( response.getVersion().equals("1.0") && response.getHeader().getField("content-length") == null){
					response.getHeader().setField("connection", "close");
					request.getHeader().setField("connection", "close");
					mustCloseServerConnection = true;
				}
				if(request.mustCloseConnection()) {
					response.getHeader().setField("connection", "close");
				}

				response.setVersion("1.1");

				// Filtering response.
				HttpResponseFilter responseFilter = new HttpResponseFilter(clientSocket, serverSocket, request, response, logger);

				if(responseFilter.filter()) {
					closeClientSocket();
					return;
				}

				// Writing filtered response to the client.
				try {
					responseFilter.filterAndWriteContent(responseParser, new MonitoredOutputStream(clientSocket.getOutputStream(), true));
				} catch(IOException e) {
					closeClientSocket();
					closedConnection = true;
					mustCloseServerConnection = true;
				}
				
				System.out.println("Received response:");
				System.out.println("From host: " + request.getEffectiveHost());
				System.out.println("Path: " + request.getEffectivePath());
				System.out.println("Status code: " + response.getStatusCode());
				System.out.println("");

				// Finished.
				if(mustCloseServerConnection) {
					serverSocket.close();
				}

				if(request.mustCloseConnection()) {
					closedConnection = true;
					closeClientSocket();
				}

			} catch(Exception e) {
				e.printStackTrace();
				closedConnection = true;
				closeClientSocket();
			} finally {
				if(serverSocket != null) {
					serverManager.finishedRequest(serverSocket);
				}
			}
		}

	}


	private void sendErrorResponse() throws IOException {
		byte[] bytes;
		bytes = response.toString().getBytes(Charset.forName("US-ASCII"));
		new MonitoredOutputStream(clientSocket.getOutputStream(), true).write(bytes);
		logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
		closeClientSocket();
	}


	private void listenAndParseResponse() throws IOException {
		responseParser = new HttpResponseParser(new MonitoredInputStream(serverSocket.getInputStream(), false));
		response = responseParser.parse();
		logger.logResponse(clientSocket.getInetAddress(), response, request);
	}


	private void listenAndParseRequest() throws IOException {
		requestParser = new HttpRequestParser(new MonitoredInputStream(clientSocket.getInputStream(), true));
		request = requestParser.parse();
		logger.logRequest(clientSocket.getInetAddress(), request);

		//Doing this to be able to establish a connection with the origin server
		request.getHeader().removeField("connection");
	}


	private void generateServerSocket() throws IOException,
	MissingHostException, UnknownHostException {
		generateServerSocket(false);
	}

	private void generateServerSocket(boolean emergency) throws IOException,
	MissingHostException, UnknownHostException {
		if( configuration.isProxied()){
			if(!emergency) {
				serverSocket = serverManager.getSocket(configuration.getChainingProxyAddress(), configuration.getChainingProxyPort());
			} else {
				serverSocket = serverManager.getEmergencySocket(configuration.getChainingProxyAddress(), configuration.getChainingProxyPort());
			}
			request.setPath("http://" + request.getEffectiveHost() + request.getEffectivePath());
		} else {
			String host = request.getEffectiveHost();
			if(!emergency) {
				serverSocket = serverManager.getSocket(InetAddress.getByName(host), request.getPort());
			} else {
				serverSocket = serverManager.getEmergencySocket(InetAddress.getByName(host), request.getPort());
			}
		}
	}


	private void writeHttpPacket(HttpPacket packet, HttpParser parser, OutputStream outputStream, boolean writeContent, boolean isClient) throws IOException {
		byte[] bytes = packet.toString().getBytes(Charset.forName("US-ASCII"));
		outputStream.write(bytes);
		String transferEncoding = packet.getHeader().getField("transfer-encoding");

		if(transferEncoding != null) {
			if(transferEncoding.toLowerCase().equals("chunked")) {
				byte[] temp;

				while((temp = parser.readNextChunk()) != null) {
					outputStream.write(temp);
				}
			}

		} else if(packet.getHeader().getField("content-length") != null || writeContent) {

			byte[] temp = new byte[1024];
			int readBytes;

			while((readBytes = parser.readNextNBodyBytes(temp, 0, 1024)) != -1) {
				outputStream.write(temp, 0, readBytes);
			}

		}
	}


	private void closeClientSocket() {
		if(clientSocket != null) {
			try {
				clientSocket.close();
			} catch (IOException e) {
				// Do nothing
			}
		}
	}
}
