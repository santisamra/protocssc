package org.cssc.prototpe.httpserver.model;

import java.io.IOException;
import java.net.Socket;

import org.cssc.prototpe.http.HttpRequest;

public class HttpServletRequest {

	private HttpRequest request;
	private Socket socket;
	
	public HttpServletRequest(HttpRequest request, Socket socket){
		this.request = request;
		this.socket = socket;
	}
	
	public HttpRequest getActualRequest(){
		return request;
	}
	public String getContent() throws IOException{
		String contentType = request.getHeader().getField("content-type");
		if( contentType == null ){
			return null;
		}
		
		if( !contentType.startsWith("multipart/form-data;")){
			return null;
		}
		
		String boundary;
		try{
		//TODO: check if every browser does this parche
		boundary = "--" + contentType.substring(contentType.indexOf("boundary=")+"boundary=".length());
		} catch(IndexOutOfBoundsException e){
			return null;
		}
		
		String contentLengthStr = request.getHeader().getField("content-length");
		int contentLength;
		try{
			contentLength = Integer.valueOf(contentLengthStr);
		} catch(NumberFormatException e){
			return null;
		}
		
		int bytesLeft = contentLength;
		StringBuffer rawContent = new StringBuffer();
		char[] charbuf = new char[1024];
		while( bytesLeft > 0 ){
			byte[] buf = new byte[1024];
			if( bytesLeft >= 1024){
				socket.getInputStream().read(buf, 0, 1024);
				bytesLeft -= 1024;
			} else {
				socket.getInputStream().read(buf, 0, bytesLeft%1024);
				bytesLeft -= bytesLeft%1024;
			}
			for(int i = 0; i < 1024; charbuf[i] = (char) buf[i++]);
			rawContent.append(charbuf);
		}
		int start = rawContent.indexOf(boundary);
		int end = rawContent.lastIndexOf(boundary);
		String content = rawContent.substring(start + boundary.length(), end);
		System.out.println(content);
		
		
		
		return null;
	}
}
