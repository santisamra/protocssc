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
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
//		buffer.append("HTTP/" + getVersion() + " " + statusCode.getCode() + " " + reasonPhrase + "\r\n");
//		
//		for(String key: getHeader().getContentMap().keySet()) {
//			buffer.append(key + ": " + getHeader().getContentMap().get(key) + "\r\n");
//		}
//		
//		buffer.append("\r\n");
		buffer.append(new String(content));
		
		return buffer.toString();
		
	}
}
