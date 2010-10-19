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
	
	public boolean mustCloseConnection() {
		if(!getVersion().equals("1.0")) {
			if(getHeader().containsField("connection")) {
				if(getHeader().getField("connection").contains("close")) {
					return true;
				} else {
					return false;
				}
			} else {
				return false;
			}
		}
		return true;
	}
}
