package org.cssc.prototpe.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;

import org.cssc.prototpe.configuration.filters.HttpRequestFilter;
import org.cssc.prototpe.configuration.filters.HttpResponseFilter;
import org.cssc.prototpe.http.HttpPacket;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.http.exceptions.InvalidMethodStringException;
import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;
import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.net.interfaces.ServerManager;
import org.cssc.prototpe.parsers.HttpParser;
import org.cssc.prototpe.parsers.HttpRequestParser;
import org.cssc.prototpe.parsers.HttpResponseParser;
import org.cssc.prototpe.parsers.exceptions.HttpParserException;

//TODO GENERAL: el control de errores ES MALISIMO

public class HttpProxyHandler implements ClientHandler{

	private static final int HTTP_PORT = 80;

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

				// READING REQUEST
				System.out.println("Por parsear.");
				try {
					listenAndParseRequest();
				} catch(HttpParserException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST);
					clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
					logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
					closeClientSocket();
					return;
				} catch(InvalidMethodStringException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.NOT_IMPLEMENTED);
					clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
					logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
					closeClientSocket();
					return;
				} catch(SocketTimeoutException e) {
					closeClientSocket();
					return;
				}

				// FILTERING REQUEST
				HttpRequestFilter requestFilter = new HttpRequestFilter(clientSocket, request, logger);


				// GENERATING CONNECTION
				try {
					System.out.println("Parsee request para " + request.getEffectiveHost());

					//TODO: Should I resolve this host and filter banned IPs?

					if(requestFilter.filter()) {
						closeClientSocket();
						return;
					}
					
					generateServerSocket();

				} catch (MissingHostException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST);
					clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
					logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
					closeClientSocket();
					return;
				} catch (UnknownHostException e) {
					e.printStackTrace();
					response = HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST);
					clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
					logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
					closeClientSocket();
					return;
				}

				try {
					// WRITING REQUEST
					writeHttpPacket(request, requestParser, serverSocket.getOutputStream(), false);
					System.out.println("Escribi request:");
					System.out.println(request);
					try {
						// READING RESPONSE
						System.out.println("Written request, awaiting response");
						listenAndParseResponse();
						System.out.println("Got response");
					} catch(InvalidStatusCodeException e) {
						// Invalid status code for the HTTP response.
						e.printStackTrace();
						response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
						clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
						logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
						closeClientSocket();
						return;
					} catch(HttpParserException e) {
						// Invalid response format.
						e.printStackTrace();
						response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
						clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
						logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
						closeClientSocket();
						return;
					}
				} catch(IOException e2) {
					// Unidentified error when writing request/reading response
					// Must retry only once, with a new socket
					try {
						serverSocket.close();
						generateServerSocket(true); // EMERGENCY SOCKET: always unused before
						// REWRITING REQUEST
						writeHttpPacket(request, requestParser, serverSocket.getOutputStream(), false);
						try {
							// READING RESPONSE
							System.out.println("Written request, awaiting response");
							listenAndParseResponse();
							System.out.println("Got response");
						} catch(InvalidStatusCodeException e) {
							// Invalid status code for the HTTP response.
							e.printStackTrace();
							response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
							clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
							logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
							closeClientSocket();
							return;
						} catch(HttpParserException e) {
							// Invalid response format.
							e.printStackTrace();
							response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
							clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
							logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
							closeClientSocket();
							return;
						}
					} catch(Exception e1) {
						// Unidentified error when trying to write request or read response from server.
						e1.printStackTrace();
						response = HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY);
						clientSocket.getOutputStream().write(response.toString().getBytes(Charset.forName("US-ASCII")));
						logger.logErrorResponse(clientSocket.getInetAddress(), response, request);
						closeClientSocket();
						return;
					}
				}



				boolean mustCloseServerConnection = response.mustCloseConnection();
				response.getHeader().removeField("connection");
				if(request.mustCloseConnection()) {
					response.getHeader().setField("connection", "close");
				}
				//TODO: somebody help me if the request was 1.0
				response.setVersion("1.1");

				// FILTERING RESPONSE
				HttpResponseFilter responseFilter = new HttpResponseFilter(clientSocket, serverSocket, request, response, logger);

				if(responseFilter.filter()) {
					closeClientSocket();
					return;
				}

				// WRITING RESPONSE

				try {
					System.out.println("Calling filter and write content");
					responseFilter.filterAndWriteContent(responseParser, clientSocket.getOutputStream());
				} catch(IOException e) {
					closeClientSocket();
					closedConnection = true;
					mustCloseServerConnection = true;
				}

				// FINISHED!
				System.out.println("Response:");
				System.out.println(response.toString());
				System.out.println("Done!");
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


	private void listenAndParseResponse() throws IOException {
		responseParser = new HttpResponseParser(serverSocket.getInputStream());
		response = responseParser.parse();
		logger.logResponse(clientSocket.getInetAddress(), response, request);
	}


	private void listenAndParseRequest() throws IOException {
		requestParser = new HttpRequestParser(clientSocket.getInputStream());
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
			System.out.println(request.getEffectivePath());
			System.out.println(request.getEffectiveHost());
			request.setPath("http://" + request.getEffectiveHost() + request.getEffectivePath());
		} else {
			System.out.println(request.getEffectivePath());
			String host = request.getEffectiveHost();
			if(!emergency) {
				serverSocket = serverManager.getSocket(InetAddress.getByName(host), HTTP_PORT);
			} else {
				serverSocket = serverManager.getEmergencySocket(InetAddress.getByName(host), HTTP_PORT);
			}
			//TODO: HAY QUE SACAR ESTOOOO
			//						serverSocket.setSoTimeout(3000);
		}
	}


//	private void print(byte[] buffer){
//		System.out.print("\"");
//		for( int i = 0; i < buffer.length && buffer[i] != 0; i++){
//			System.out.print((char)buffer[i]);
//		}
//		System.out.print("\"");
//	}


	private void writeHttpPacket(HttpPacket packet, HttpParser parser, OutputStream outputStream, boolean writeContent) throws IOException {
		outputStream.write(packet.toString().getBytes(Charset.forName("US-ASCII")));
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
				//TODO: what to do here?
				e.printStackTrace(); 
			}
		}
	}
}
