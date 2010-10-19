package org.cssc.prototpe.net;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;

import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpPacket;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.http.exceptions.InvalidMethodStringException;
import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;
import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.parsers.HttpParser;
import org.cssc.prototpe.parsers.HttpRequestParser;
import org.cssc.prototpe.parsers.HttpResponseParser;

//TODO GENERAL: el control de errores ES MALISIMO

public class HttpProxyHandler implements ClientHandler{

	private Socket clientSocket;
	private Socket serverSocket;
	private HttpRequestParser requestParser;
	private HttpResponseParser responseParser;
	private HttpRequest request;
	private HttpResponse response;

	private Logger logger;
	private ServerManager serverManager;

	public HttpProxyHandler() {
		this.logger = Application.getInstance().getLogger();
		this.serverManager = Application.getInstance().getServerManager();
	}


	@Override
	public void handle(Socket socket) {
		this.clientSocket = socket;
		boolean closedConnection = false;
		while(!closedConnection) {
			serverSocket = null;
			InetAddress serverAddress = null;
			try {
				try {
					requestParser = new HttpRequestParser(socket.getInputStream());
					request = requestParser.parse();
					logger.logRequest(socket.getInetAddress(), request);

					//TODO: Ask for the socket to someone
					String host = request.getEffectiveHost();

					//TODO: Should I resolve this host and filter banned IPs?
					serverAddress = InetAddress.getByName(host);
					serverSocket = serverManager.getSocket(serverAddress);

					response = null;
					
					try {
						writeHttpPacket(request, requestParser, serverSocket.getOutputStream());

						//Should block here when the parser attempts reading from this input stream
						responseParser = new HttpResponseParser(serverSocket.getInputStream());

						response = responseParser.parse();
					} catch(IOException e) {
						//TODO: Problem with the server connection!
						e.printStackTrace();
						serverSocket.close();
						HttpResponse response = new HttpResponse("1.1", new HttpHeader(), HttpResponseCode.BAD_GATEWAY, "Bad request", new byte[0]);
						clientSocket.getOutputStream().write(response.toString().getBytes());
					}

					if(response != null) {
						logger.logResponse(socket.getInetAddress(), response, request);

						writeHttpPacket(response, responseParser, clientSocket.getOutputStream());

						if(response.mustCloseConnection()) {
							serverSocket.close();
						}

						if(request.mustCloseConnection()) {
							clientSocket.close();
							closedConnection = true;
							System.out.println("Thread " + Thread.currentThread() + " closed client socket: " + clientSocket);
							System.out.println("---------------");
						}
					}

				} catch(MissingHostException e) {
					e.printStackTrace();
					HttpResponse response = new HttpResponse("1.1", new HttpHeader(), HttpResponseCode.BAD_REQUEST, "Bad request", new byte[0]);
					clientSocket.getOutputStream().write(response.toString().getBytes());
				} catch(InvalidStatusCodeException e) {
					e.printStackTrace();
					HttpResponse response = new HttpResponse("1.1", new HttpHeader(), HttpResponseCode.BAD_GATEWAY, "Bad request", new byte[0]);
					clientSocket.getOutputStream().write(response.toString().getBytes());
				} catch(InvalidMethodStringException e) {
					e.printStackTrace();
					HttpResponse response = new HttpResponse("1.1", new HttpHeader(), HttpResponseCode.NOT_IMPLEMENTED, "Not implemented", new byte[0]);
					clientSocket.getOutputStream().write(response.toString().getBytes());
				}
				
				serverManager.finishedRequest(serverAddress);

				//TODO: don't close if this is a keep-alive connection

				//			if(serverSocket != null) {
				//				serverSocket.close();
				//			}


			} catch (IOException e){
//								e.printStackTrace();
				System.out.println("Here");
				try {
					if(serverAddress != null) {
						serverManager.finishedRequest(serverAddress);
					}
					clientSocket.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				closedConnection = true;
			}

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

		}
	}

}
