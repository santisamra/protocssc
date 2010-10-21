package org.cssc.prototpe.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import org.cssc.prototpe.http.HttpPacket;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.http.exceptions.InvalidMethodStringException;
import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;
import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.filters.SocketFilter;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.net.interfaces.ServerManager;
import org.cssc.prototpe.parsers.HttpParser;
import org.cssc.prototpe.parsers.HttpRequestParser;
import org.cssc.prototpe.parsers.HttpResponseParser;
import org.cssc.prototpe.parsers.exceptions.InvalidPacketException;
import org.cssc.prototpe.parsers.exceptions.InvalidPacketParsingException;

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
		SocketFilter socketFilter = new SocketFilter(socket);

		boolean socketFiltering = false;
		try {
			socketFiltering = socketFilter.filter(clientSocket);
		} catch (IOException e2) {
			e2.printStackTrace();
		}

		if(!socketFiltering) {
			boolean closedConnection = false;
			while(!closedConnection) {
				serverSocket = null;
				response = null;
				try {
					System.out.println("Por parsear.");
					listenAndParseRequest();
					try {
						System.out.println("Parsee request para " + request.getEffectiveHost());

						//TODO: Should I resolve this host and filter banned IPs?

						generateServerSocket();

					} catch (MissingHostException e) {
						e.printStackTrace();
						clientSocket.getOutputStream().write(HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST).toString().getBytes());					
					}

					try {
						try {
							System.out.println("About to write request");
							writeHttpPacket(request, requestParser, serverSocket.getOutputStream());
						} catch(IOException e) {
							// Must retry only once
							try {
								generateServerSocket();
								writeHttpPacket(request, requestParser, serverSocket.getOutputStream());
							} catch(Exception e1) {
								e1.printStackTrace();
								clientSocket.getOutputStream().write(HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY).toString().getBytes());							
							}
						}
						System.out.println("Written request, awaiting response");
						listenAndParseResponse();
						System.out.println("Got response");
					} catch(InvalidPacketParsingException e) {
						//TODO: Problem with the server connection!
						e.printStackTrace();
						clientSocket.getOutputStream().write(HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST).toString().getBytes());
					} catch(InvalidPacketException e) {
						e.printStackTrace();
						clientSocket.getOutputStream().write(HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY).toString().getBytes());
					} catch(InvalidStatusCodeException e) {
						e.printStackTrace();
						clientSocket.getOutputStream().write(HttpResponse.emptyResponse(HttpResponseCode.BAD_GATEWAY).toString().getBytes());
					} catch(InvalidMethodStringException e) {
						e.printStackTrace();
						clientSocket.getOutputStream().write(HttpResponse.emptyResponse(HttpResponseCode.NOT_IMPLEMENTED).toString().getBytes());
					}

					writeHttpPacket(response, responseParser, clientSocket.getOutputStream());
					
					serverManager.finishedRequest(serverSocket);
					//TODO: parche
					if(!clientSocket.isClosed()) {
						closedConnection = true;
						clientSocket.close();
					}


				} catch (IOException e){
					//	e.printStackTrace();
					System.out.println("Here");
					try {
						if(serverSocket != null) {
							serverManager.finishedRequest(serverSocket);
						}
						clientSocket.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					closedConnection = true;
				}
			}
		}

	}


	private void listenAndParseResponse() throws IOException {
		responseParser = new HttpResponseParser(serverSocket.getInputStream());
		response = responseParser.parse();
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
		if( configuration.isProxied()){
			serverSocket = serverManager.getSocket(configuration.getProxy(), configuration.getProxyPort());
			System.out.println(request.getEffectivePath());
			System.out.println(request.getEffectiveHost());
			request.setPath("http://" + request.getEffectiveHost() + request.getEffectivePath());
		} else {
			System.out.println(request.getEffectivePath());
			//TODO: Ask for the socket to someone
			String host = request.getEffectiveHost();

			serverSocket = serverManager.getSocket(InetAddress.getByName(host), HTTP_PORT);
			//TODO: HAY QUE SACAR ESTOOOO
			//						serverSocket.setSoTimeout(3000);
		}
	}


	private void print(byte[] buffer){
		System.out.print("\"");
		for( int i = 0; i < buffer.length && buffer[i] != 0; i++){
			System.out.print((char)buffer[i]);
		}
		System.out.print("\"");
	}


	private void writeHttpPacket(HttpPacket packet, HttpParser parser, OutputStream outputStream) throws IOException {
		outputStream.write(packet.toString().getBytes());
		System.out.println(packet);
		String transferEncoding = packet.getHeader().getField("transfer-encoding");

		if(transferEncoding != null) {
			if(transferEncoding.toLowerCase().equals("chunked")) {
				byte[] temp;

				while((temp = parser.readNextChunk()) != null) {

					try {
						outputStream.write(temp);
					} catch(SocketException e) {
						System.out.println("The client has closed his socket side.");
						break;
					}
				}
			}

		} else if(packet.getHeader().getField("content-length") != null) {

			byte[] temp = new byte[1024];
			int readBytes;

			while((readBytes = parser.readNextNBodyBytes(temp, 0, 1024)) != -1) {
				try {
					outputStream.write(temp, 0, readBytes);
				} catch(SocketException e) {
					System.out.println("The client has closed his socket side.");
					break;
				}
			}

		} else {
			if(packet instanceof HttpResponse) {
				byte[] temp = new byte[1024];
				int readBytes;

				while((readBytes = parser.readNextNBodyBytes(temp, 0, 1024)) != -1) {
					try {
						print(temp);
						outputStream.write(temp, 0, readBytes);
					} catch(SocketException e) {
						System.out.println("The client has closed his socket side.");
						break;
					}
				}
			}

		}
	}

}
