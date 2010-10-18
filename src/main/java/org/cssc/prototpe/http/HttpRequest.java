package org.cssc.prototpe.http;

import java.util.Map.Entry;

import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.exceptions.FatalException;

public class HttpRequest extends HttpPacket {
	
	private String path;
	private HttpMethod method;

	public HttpRequest(String version, HttpHeader header, String path, HttpMethod method) {
		super(version, header);
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
	
	/**
	 * Returns a String with the host name to connect to,
	 * preferrably from the HTTP request path; if impossible,
	 * then it is taken from the "Host" header.
	 * Throws an exception if it cannot find a host.
	 */
	public String getEffectiveHost() throws MissingHostException {
		if(hasAbsolutePath()) {
			String temp = getPath().substring(7);
			return temp.substring(0, temp.indexOf("/"));
		} else {
			String headerHost = getHeader().getField("Host");
			if( headerHost == null ){
				throw new MissingHostException();
			}
			return headerHost;
		}
		
	}
	
	/**
	 * Returns the effective, path-only section of the path, including the first /.
	 */
	public String getEffectivePath() {
		if(hasAbsolutePath()) {
			try {
				String path = getPath();
				String effHost = getEffectiveHost();
				return path.substring(path.indexOf(effHost) + effHost.length());
			} catch (MissingHostException e) {
				throw new FatalException(e);
			}
		} else {
			return getPath();
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(this.method.toString() + " " + this.getEffectivePath() + " HTTP/" + this.getVersion() + "\n");
		
		for(Entry<String,String> e: this.getHeader().getMap().entrySet() ){
			buffer.append(e.getKey() + ": " + e.getValue() + "\n");
		}
		
		buffer.append("\n");
		return buffer.toString();
	}
}
