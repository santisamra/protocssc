package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.http.exceptions.InvalidMethodStringException;
import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;
import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.parsers.HttpRequestParser;
import org.cssc.prototpe.parsers.HttpResponseParser;

public class HttpProxyHandler implements ClientHandler{

	private Socket clientSocket;
	private Socket serverSocket;
	private HttpRequestParser requestParser;
	private HttpResponseParser responseParser;
	private HttpRequest request;
	private HttpResponse response;
	private Logger logger;

	public HttpProxyHandler() {
		this.logger = Application.getInstance().getLogger();
	}


	@Override
	public void handle(Socket socket) {
		this.clientSocket = socket;
		try{
			try {
				requestParser = new HttpRequestParser(socket.getInputStream());
				request = requestParser.parse();
				logger.logRequest(socket.getInetAddress(), request);

				//TODO: Ask for the socket to someone
				String host = request.getEffectiveHost();

				//TODO: Should I resolve this host and filter banned IPs?
				serverSocket = new Socket(host, 80);

				serverSocket.getOutputStream().write(request.toString().getBytes());

				print(request.toString().getBytes());
				System.out.println("Thread " + Thread.currentThread() + " sent request: ");
				System.out.println("Method: " + request.getMethod());
				System.out.println("Host: " + request.getHeader().getField("host"));
				System.out.println("Path: " + request.getPath());
				System.out.println("Client socket: " + socket);
				System.out.println("---------------");

				//Should block here when the parser attempts reading from this input stream
				responseParser = new HttpResponseParser(serverSocket.getInputStream());

				//TODO: uncomment this when this functionality is reached.
				response = responseParser.parse();

				System.out.println("Thread " + Thread.currentThread() + " got response: ");
				System.out.println("Status code: " + response.getStatusCode());
				System.out.println("For request path: " + request.getPath());
				System.out.println("---------------");

				//			System.out.println(response.getContent().length);
				//			System.out.println(response.getHeader().getField("content-length"));

				logger.logResponse(socket.getInetAddress(), response, request);

				//TODO: process the response here


				//TODO: implement toString correctly in the response.. 
				//(Will it work although we are going through a string?)
				//				clientSocket.getOutputStream().write(response.toBytes());

				/* Response first-line and header fields and firstly sent to client. */
				clientSocket.getOutputStream().write(firstResponseBytes(response));


				String transferEncoding = response.getHeader().getField("transfer-encoding");

				if(transferEncoding != null) {
					if(transferEncoding.toLowerCase().equals("chunked")) {
						byte[] temp;

						while((temp = responseParser.readNextChunk()) != null) {

							try {
								clientSocket.getOutputStream().write(temp);
							} catch(SocketException e) {
								System.out.println("The client has closed his socket side.");
								break;
							}
						}
					}
					
				} else if(response.getHeader().getField("content-length") != null) {

					byte[] temp = new byte[1024];
					int readBytes;

					while((readBytes = responseParser.readNextNBodyBytes(temp, 0, 1024)) != -1) {
						try {
							clientSocket.getOutputStream().write(temp, 0, readBytes);
						} catch(SocketException e) {
							System.out.println("The client has closed his socket side.");
							break;
						}
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

			//TODO: don't close if this is a keep-alive connection

			if(serverSocket != null) {
				serverSocket.close();
			}

			clientSocket.close();
			System.out.println("Thread " + Thread.currentThread() + " closed client socket: " + clientSocket);
			System.out.println("---------------");

		} catch (IOException e){
			e.printStackTrace();
		}


	}


	private void print(byte[] buffer){
		System.out.print("\"");
		for( int i = 0; i < buffer.length && buffer[i] != 0; i++){
			System.out.print((char)buffer[i]);
		}
		System.out.print("\"");
	}


	private byte[] firstResponseBytes(HttpResponse response) {
		StringBuffer buffer = new StringBuffer();

		buffer.append("HTTP/" + response.getVersion() + " " + response.getStatusCode().getCode() + " " + response.getReasonPhrase() + "\r\n");

		for(String key: response.getHeader().getMap().keySet()) {
			buffer.append(key + ": " + response.getHeader().getMap().get(key) + "\r\n");
		}

		buffer.append("\r\n");

		return buffer.toString().getBytes();
	}

}
