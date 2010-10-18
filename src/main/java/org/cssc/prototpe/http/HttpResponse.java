package org.cssc.prototpe.http;

public class HttpResponse extends HttpPacket {

	private HttpResponseCode statusCode;
	private String reasonPhrase;
	private byte[] content;
	
	public HttpResponse(String version, HttpHeader header, HttpResponseCode statusCode, String reasonPhrase, byte[] content) {
		super(version, header);
		this.statusCode = statusCode;
		this.reasonPhrase = reasonPhrase;
		this.content = content;
	}
	
	public HttpResponseCode getStatusCode() {
		return statusCode;
	}
	
	public String getReasonPhrase() {
		return reasonPhrase;
	}
	
	public byte[] getContent() {
		return content;
	}
	
	public void setContent(byte[] content) {
		this.content = content;
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
}
