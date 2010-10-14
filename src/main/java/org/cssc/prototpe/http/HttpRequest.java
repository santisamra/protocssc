package org.cssc.prototpe.http;

import java.util.Map.Entry;

public class HttpRequest extends HttpPacket {
	
	private String path;
	private HttpMethod method;

	public HttpRequest(String version, String path, HttpMethod method) {
		super(version);
		this.path = path;
		this.method = method;
	}
	
	public String getPath() {
		return path;
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
	//TODO: Determine if this is correct.. I don't like it AT ALL
	public boolean hasAbsolutePath(){
		
		if( getPath().startsWith("/") ){
			return false;
		} else {
			return true;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.method.toString() + " " + this.getPath() + " HTTP/" + this.getVersion() + "\n");
		
		for(Entry<String,String> e: this.getHeader().getContentMap().entrySet() ){
			buffer.append(e.getKey() + ": " + e.getValue() + "\n");
		}
		
		buffer.append("\n");
		return buffer.toString();
	}
}
