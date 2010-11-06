package org.cssc.prototpe.httpserver.model;

import org.cssc.prototpe.http.HttpResponse;

public class HttpServletResponse {
	
	private HttpResponse response;
	private StringBuffer buffer;
	private int contentLength;
	
	public HttpServletResponse(){
		buffer = new StringBuffer();
	}
	
	public StringBuffer getBuffer(){
		return buffer;
	}
	
	public void setBuffer(StringBuffer buffer){
		this.buffer = buffer;
	}
	
	public HttpResponse getActualResponse(){
		return response;
	}
	
	public void setHttpResponse(HttpResponse response){
		this.response = response;
	}
	
	public int getContentLength(){
		return contentLength;
	}
	public void setContentLength(int contentLength){
		this.contentLength = contentLength;
	}

}
