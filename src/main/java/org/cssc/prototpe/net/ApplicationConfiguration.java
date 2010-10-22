package org.cssc.prototpe.net;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import org.cssc.prototpe.net.filters.application.ApplicationFilter;

public class ApplicationConfiguration {
	
	private int threadPoolSize;
	private int maxPersistantServerConnections;
	private String loggingFileName;
	private InetAddress proxy;
	private int proxyport;
	private int clientKeepAliveTimeout;
	
	public int getClientKeepAliveTimeout() {
		return clientKeepAliveTimeout;
	}

	public void setClientKeepAliveTimeout(int clientKeepAliveTimeout) {
		this.clientKeepAliveTimeout = clientKeepAliveTimeout;
	}

	private List<ApplicationFilter> filters;
	
	public ApplicationConfiguration() {
		this.filters = new LinkedList<ApplicationFilter>();
	}
	
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
	
	public void addFilter(ApplicationFilter filter) {
		filters.add(filter);
	}
	
	public ApplicationFilter getFilterForCondition(InetAddress ip, String userAgent) {
		for(ApplicationFilter f: filters) {
			List<InetAddress> ips = f.getCondition().getIps();
			String browser = f.getCondition().getBrowser();
			String operatingSystem = f.getCondition().getOperatingSystem();
			
			if((ips != null && ips.contains(ip) ||
			   (browser != null && userAgent != null && userAgent.contains(browser)) ||
			   (operatingSystem != null && userAgent != null && userAgent.contains(operatingSystem)))) {
				
				return f;
			}
		}
		
		return null;
	}
	
}
