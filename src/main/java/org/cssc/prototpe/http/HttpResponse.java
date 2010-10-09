package org.cssc.prototpe.http;

public class HttpResponse extends HttpPacket {

	private HttpResponseCode responseCode;
	
	public HttpResponse(String version, HttpResponseCode responseCode) {
		super(version);
		this.responseCode = responseCode;
	}
	
	public HttpResponseCode getResponseCode() {
		return responseCode;
	}
}
