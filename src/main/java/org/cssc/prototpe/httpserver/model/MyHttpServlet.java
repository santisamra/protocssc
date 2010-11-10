package org.cssc.prototpe.httpserver.model;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;

import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.net.exceptions.FatalException;



public abstract class MyHttpServlet {
	
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	public void setResponse(HttpServletResponse response){
		this.response = response;
	}
	
	public HttpServletResponse getResponse(){
		return response;
	}
	
	public void setRequest(HttpServletRequest request){
		this.request = request;
	}
	
	public HttpServletRequest getRequest(){
		return request;
	}
	
	public abstract void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	public abstract void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	private StringBuffer getResource(String file) throws IOException{
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
		
		response.setContentLength(contentLength);
		
		return buffer;
	}
	
	public boolean setResponse() throws IOException{
		if( response.isUnauthorized() ){
			return false;
		}
		if( response.isRedirected() ){
			HttpHeader header = new HttpHeader();
			header.setField("Location", response.getRedirect());
			response.setHttpResponse(new HttpResponse("1.1", header, HttpResponseCode.FOUND, "OK"));
		} else if( response.isForwarded() ){
			StringBuffer buffer = getResource(response.getForward());
			response.setBuffer(buffer);
			HttpHeader header = new HttpHeader();
			header.setField("content-length", String.valueOf(response.getContentLength()));
			header.setField("connection", "close");
			HttpResponse resp = new HttpResponse("1.1", header, HttpResponseCode.OK, "OK");
			response.setHttpResponse(resp);
		} else {
			StringBuffer buffer = response.getBuffer();
			if( buffer == null || buffer.length() == 0){
				throw new IllegalStateException("There is no output for this request (no buffer and no forward or redirect).");
			} else {
				response.setContentLength(buffer.toString().getBytes(Charset.forName("US-ASCII")).length);
			}
			response.setBuffer(buffer);
			HttpHeader header = new HttpHeader();
			header.setField("content-length", String.valueOf(response.getContentLength()));
			header.setField("connection", "close");
			HttpResponse resp = new HttpResponse("1.1", header, HttpResponseCode.OK, "OK");
			response.setHttpResponse(resp);
		}
		return true;
	}
	
	private static final int MIN_AUTH_LENGTH = 7;
	
	protected Authorization getAuthorization(){
		String encodedAuth = request.getActualRequest().getHeader().getField("authorization");
		if( encodedAuth == null ){
			return null;
		}
		
		if( !encodedAuth.startsWith("Basic ") || encodedAuth.length() < MIN_AUTH_LENGTH){
			return null;
		}
		encodedAuth = encodedAuth.substring(encodedAuth.indexOf(' ') + 1);
		
		char[] encodedAuthChars = encodedAuth.toCharArray();
		byte[] decodedAuth = Base64Decoder.decode(encodedAuthChars, 0, encodedAuthChars.length);
		StringBuffer decodedAuthString = new StringBuffer();
		for(byte b: decodedAuth){
			decodedAuthString.append((char)b);
		}
		
		String username = decodedAuthString.substring(0, decodedAuthString.indexOf(":"));
		String password = decodedAuthString.substring(decodedAuthString.indexOf(":") + 1);
		
		return new Authorization(username, password);
		
	}
	
	protected boolean validateAuth(){
		Authorization auth = getAuthorization();
		Authorizer authorizer = Authorizer.getInstance();
		if( auth == null || !authorizer.authorize(auth)){
			response.unauthorize();
			return false;
		}
		return true;
	}
	
	
}
