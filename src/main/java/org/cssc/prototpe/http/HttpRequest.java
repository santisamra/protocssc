package org.cssc.prototpe.http;

import java.util.Map.Entry;

import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.parsers.exceptions.HttpParserException;

public class HttpRequest extends HttpPacket {
	
	private static final int HTTP_DEFAULT_PORT = 80;
	
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
	
	public void setPath(String path){
		this.path = path;
	}
	
	public HttpMethod getMethod() {
		return method;
	}
	
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
			temp = temp.substring(0, temp.indexOf("/"));
			if( hasPort(temp) ){
				return temp.substring(0, temp.indexOf(":"));
			}
			return temp;
		} else {
			String headerHost = getHeader().getField("host");
			if( headerHost == null ){
				throw new MissingHostException();
			}
			if( hasPort(headerHost)){
				return headerHost.substring(0, headerHost.indexOf(":"));
			}
			return headerHost;
		}
		
	}
	
	public Integer getPort(){
		String path = getPath();
		if( hasAbsolutePath()){
			path = path.substring(7);
			path = path.substring(0, path.indexOf("/"));
			if( hasPort(path) ){
				try{
					return Integer.valueOf(path.substring(path.indexOf(":") + 1));
				} catch (NumberFormatException e) {
					throw new HttpParserException(e);
				}
			}
		}
		return HTTP_DEFAULT_PORT;
	}
	
	/**
	 * Must recieve the URL without the http://
	 * @param path
	 * @return
	 */
	public boolean hasPort(String path){
		return path.contains(":");
	}
	
	/**
	 * Returns the effective, path-only section of the path, including the first /.
	 */
	public String getEffectivePath() {
		if(hasAbsolutePath()) {
			String path = getPath();
			path = path.substring(7);
			return path.substring(path.indexOf("/"));
		} else {
			return getPath();
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		if( Application.getInstance().getApplicationConfiguration().isProxied()){
			try {
				buffer.append(this.method.toString() + " http://" + this.getEffectiveHost() + this.getEffectivePath() + " HTTP/" + this.getVersion() + "\r\n");
			} catch (MissingHostException e1) {
				e1.printStackTrace();
				return null;
			}
		} else {
			buffer.append(this.method.toString() + " " + this.getEffectivePath() + " HTTP/" + this.getVersion() + "\r\n");
		}
		
		if(!getHeader().containsField("host")) {
			try {
				getHeader().setField("host", getEffectiveHost());
			} catch (MissingHostException e) {
				e.printStackTrace();
			}
		}
		
		for(Entry<String,String> e: this.getHeader().getMap().entrySet() ){
			buffer.append(e.getKey() + ": " + e.getValue() + "\r\n");
		}
		
		buffer.append("\r\n");
		return buffer.toString();
	}
}
