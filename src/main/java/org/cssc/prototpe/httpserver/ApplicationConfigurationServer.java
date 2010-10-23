package org.cssc.prototpe.httpserver;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpMethod;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.ApplicationConfiguration;
import org.cssc.prototpe.net.exceptions.FatalException;
import org.cssc.prototpe.parsers.HttpRequestParser;

public class ApplicationConfigurationServer implements Runnable{

	public static final String APP_CONFIG_SERVER_PAGE = "src/main/resources/html/manager/index.html";
	
	ServerSocket serverSocket;
	ApplicationConfiguration configuration;
	
	public ApplicationConfigurationServer(int port){
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new FatalException(e);
		}
		configuration = Application.getInstance().getApplicationConfiguration();
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		Socket socket;
		
		while(true){
			try {
				socket = serverSocket.accept();
				HttpRequestParser reqparser = new HttpRequestParser(socket.getInputStream());
				HttpRequest request = reqparser.parse();
				
				if( request.getMethod().equals(HttpMethod.GET) && "/".equals(request.getEffectivePath())){
					index(socket, true);
				} else if( request.getMethod().equals(HttpMethod.HEAD)){
					index(socket, false);
				} else if( request.getMethod().equals(HttpMethod.POST)){
					//TODO: Posts that modify configuration
				} else {
					badRequest(socket);
				}
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	private void index(Socket socket, boolean writeContent) throws IOException{
		//Already copied to servlet;
		FileInputStream stream;
		try{
			stream = new FileInputStream(APP_CONFIG_SERVER_PAGE);
		} catch (FileNotFoundException e){
			throw new FatalException(e);
		}
		
		int contentLength = 0;
		StringBuffer buffer = new StringBuffer();
		int read;
		
		while( (read = stream.read()) != -1){
			buffer.append((char)read);
			contentLength++;
		}
		
		HttpHeader header = new HttpHeader();
		header.setField("content-length", String.valueOf(contentLength));
		header.setField("connection", "close");
		HttpResponse response = new HttpResponse("1.1", header, HttpResponseCode.OK, "OK", new byte[0]);
		socket.getOutputStream().write(response.toString().getBytes());
		if( writeContent ){
			socket.getOutputStream().write(buffer.toString().getBytes());
		}
		socket.getOutputStream().flush();
		socket.close();
		//End of copied to servlet;
	}
	
	private void badRequest(Socket socket) throws IOException{
		HttpHeader header = new HttpHeader();
		header.setField("connection", "close");
		HttpResponse response = HttpResponse.emptyResponse(HttpResponseCode.BAD_REQUEST);
		socket.getOutputStream().write(response.toString().getBytes());
		socket.getOutputStream().flush();
		socket.close();
	}

	
}
