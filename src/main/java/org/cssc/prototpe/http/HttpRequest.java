package org.cssc.prototpe.http;

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
}
