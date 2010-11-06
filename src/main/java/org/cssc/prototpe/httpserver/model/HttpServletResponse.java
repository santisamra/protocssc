package org.cssc.prototpe.httpserver.model;

import org.cssc.prototpe.http.HttpResponse;

public class HttpServletResponse {
	
	private HttpResponse response;
	private StringBuffer buffer;
	private int contentLength;
	private String redirect;
	private String forward;
	
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
	
	public void sendRedirect(String URL){
		this.redirect = URL;
	}
	
	public String getRedirect(){
		return redirect;
	}
	
	public boolean isRedirected(){
		return redirect != null;
	}

	public void forward(String forward) {
		this.forward = forward;
	}

	public String getForward() {
		return forward;
	}
	
	public boolean isForwarded(){
		return forward != null;
	}

}
