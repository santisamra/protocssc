package org.cssc.prototpe.httpserver.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Socket;

import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.net.exceptions.FatalException;

public abstract class MyHttpServlet {
	
	private Socket socket;
	
	public abstract void doGet(HttpRequest request, HttpResponse response);
	
	public abstract void doPost(HttpRequest request, HttpResponse response);
	
	protected StringBuffer getHTMLFromFile(String file) throws IOException{
		FileInputStream stream;
		try{
			stream = new FileInputStream(file);
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
		
		return buffer;
	}
	
	public void setSocket(Socket socket){
		this.socket = socket;
	}
	
	public void sendResponse(StringBuffer buffer, int contentLength, boolean writeContent) throws IOException{
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
	}
	
	
	
	
}
