package org.cssc.prototpe.http;

public class HttpResponse extends HttpPacket {

	private HttpResponseCode statusCode;
	private String reasonPhrase;
	
	public HttpResponse(String version, HttpHeader header, HttpResponseCode statusCode, String reasonPhrase) {
		super(version, header);
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
	}
	
	public HttpResponseCode getStatusCode() {
		return statusCode;
	}
	
	public String getReasonPhrase() {
		return reasonPhrase;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();

		buffer.append("HTTP/" + getVersion() + " " + getStatusCode().getCode() + " " + getReasonPhrase() + "\r\n");

		for(String key: getHeader().getMap().keySet()) {
			buffer.append(key + ": " + getHeader().getMap().get(key) + "\r\n");
		}

		buffer.append("\r\n");

		return buffer.toString();
	}
	
	public static HttpResponse emptyResponse(HttpResponseCode responseCode) {
		HttpResponse r = new HttpResponse("1.1", new HttpHeader(), responseCode, responseCode.toString());
		r.getHeader().setField("content-length", "0");
		return r;
	}
	
}
