package org.cssc.prototpe.http;

public abstract class HttpPacket {

	private String version;
	private HttpHeader header;
	
	public HttpPacket(String version, HttpHeader header) {
		this.version = version;
		this.header = header;
	}
	
	public String getVersion() {
		return version;
	}
	
	public HttpHeader getHeader() {
		return header;
	}
}
