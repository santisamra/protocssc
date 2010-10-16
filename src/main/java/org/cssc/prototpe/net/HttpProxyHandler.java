package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.Socket;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.parsers.HttpRequestParser;
import org.cssc.prototpe.parsers.HttpResponseParser;

public class HttpProxyHandler implements ClientHandler{
	
	private static HttpProxyHandler instance = null;
	
	private Socket clientSocket;
	private Socket serverSocket;
	private HttpRequestParser requestParser;
	private HttpResponseParser responseParser;
	private HttpRequest request;
	private HttpResponse response;


	@Override
	public void handle(Socket socket) {
		this.clientSocket = socket;
		try{
			requestParser = new HttpRequestParser(socket.getInputStream());
			request = requestParser.parse();
			
			//TODO: Ask for the socket to someone
			String host;
			if( request.hasAbsolutePath()){
				//TODO: SUPER PARCHE, PONER EN LA CLASE QUE CORRESPONDE (HttpRequest)
				String temp = request.getPath().substring(7);
				host = temp.substring(0, temp.indexOf("/"));
			} else {
				String headerHost = request.getHeader().getField("Host");
				if( headerHost == null ){
					throw new IllegalStateException("Host wasn't provided.. no way of continuing this request");
				}
				host = headerHost + request.getPath();
			}
			
			//TODO: Should I resolve this host and filter banned IPs?
			serverSocket = new Socket(host, 80);
			
			serverSocket.getOutputStream().write(request.toString().getBytes());
			
			//Should block here when the parser attempts reading from this input stream
			responseParser = new HttpResponseParser(serverSocket.getInputStream());
			
			//TODO: uncomment this when this functionality is reached.
			response = responseParser.parse();
//			System.out.println(response.getContent().length);
//			System.out.println(response.getHeader().getField("content-length"));
			
			//TODO: process the response here
			
			
			//TODO: implement toString correctly in the response.. 
			//(Will it work although we are going through a string?)
			clientSocket.getOutputStream().write(response.toString().getBytes());
			
			
			//TODO: don't close if this is a keep-alive connection
			serverSocket.close();
			clientSocket.close();
			
		} catch (IOException e){
			e.printStackTrace();
		}
		
		
	}
	
	public static HttpProxyHandler getInstance() {
		if(instance == null) {
			synchronized (HttpProxyHandler.class) {
				if(instance == null) {
					instance = new HttpProxyHandler();
				}
			}
		}
		return instance;
	}

	

	
}
