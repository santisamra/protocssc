package org.cssc.prototpe.net;

import java.net.InetAddress;

public class ApplicationConfiguration {
	
	private int threadPoolSize;
	private int maxPersistantServerConnections;
	private String loggingFileName;
	private InetAddress proxy;
	private int proxyport;
	
	public int getMaxPersistantServerConnections() {
		return maxPersistantServerConnections;
	}
	
	public void setMaxPersistantServerConnections(int maxPersistantServerConnections) {
		this.maxPersistantServerConnections = maxPersistantServerConnections;
	}

	public String getLoggingFileName() {
		return loggingFileName;
	}

	public void setLoggingFileName(String loggingFileName) {
		this.loggingFileName = loggingFileName;
	}

	public int getThreadPoolSize() {
		return threadPoolSize;
	}

	public void setThreadPoolSize(int threadPoolSize) {
		this.threadPoolSize = threadPoolSize;
	}
	
	public void setProxy(InetAddress proxy, int proxyport){
		this.proxy = proxy;
		this.proxyport = proxyport;
	}
	
	public InetAddress getProxy(){
		return proxy;
	}
	
	public int getProxyPort(){
		return proxyport;
	}
	
	public boolean isProxied(){
		return proxy != null && proxyport != 0;
	}
	
}
