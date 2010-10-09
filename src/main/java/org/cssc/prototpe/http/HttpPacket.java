package org.cssc.prototpe.http;

public abstract class HttpPacket {

	private String version;
	private HttpHeader header;
	
	public HttpPacket(String version) {
		this.version = version;
		this.header = new HttpHeader();
	}
	
	public String getVersion() {
		return version;
	}
	
	public HttpHeader getHeader() {
		return header;
	}
}
