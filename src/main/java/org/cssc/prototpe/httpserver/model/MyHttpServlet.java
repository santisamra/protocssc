package org.cssc.prototpe.httpserver.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.net.exceptions.FatalException;

public abstract class MyHttpServlet {
	
	private String mapping;
	private HttpServletResponse response;
	
	public MyHttpServlet(String mapping){
		this.mapping = mapping;
	}
	
	public void setResponse(HttpServletResponse response){
		this.response = response;
	}
	
	public HttpServletResponse getResponse(){
		return response;
	}
	
	public abstract void doGet(HttpRequest request, HttpServletResponse response) throws IOException;
	
	public abstract void doPost(HttpRequest request, HttpServletResponse response) throws IOException;
	
	private StringBuffer getResource() throws IOException{
		FileInputStream stream;
		try{
			stream = new FileInputStream(mapping);
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
		
		response.setContentLength(contentLength);
		
		return buffer;
	}
	
	public void setResponse() throws IOException{
		StringBuffer buffer = response.getBuffer();
		if( buffer == null || buffer.length() == 0){
			buffer = getResource();
		} else {
			response.setContentLength(buffer.toString().getBytes(Charset.forName("US-ASCII")).length);
		}
		response.setBuffer(buffer);
		HttpHeader header = new HttpHeader();
		header.setField("content-length", String.valueOf(response.getContentLength()));
		header.setField("connection", "close");
		HttpResponse resp = new HttpResponse("1.1", header, HttpResponseCode.OK, "OK", new byte[0]);
		response.setHttpResponse(resp);
	}
	
	
	
	
}
