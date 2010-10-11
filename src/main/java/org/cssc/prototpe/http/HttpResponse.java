package org.cssc.prototpe.http;

public class HttpResponse extends HttpPacket {

	private HttpResponseCode responseCode;
	private String content;
	
	public HttpResponse(String version, HttpResponseCode responseCode, String content) {
		super(version);
		this.responseCode = responseCode;
	}
	
	public HttpResponseCode getResponseCode() {
		return responseCode;
	}
	
	public String getContent() {
		return content;
	}
}
