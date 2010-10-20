package org.cssc.prototpe.net;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import org.cssc.prototpe.net.filters.ApplicationFilter;
import org.cssc.prototpe.net.filters.FilterCondition;

public class ApplicationConfiguration {
	
	private int threadPoolSize;
	private int maxPersistantServerConnections;
	private String loggingFileName;
	private InetAddress proxy;
	private int proxyport;
	
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
	
	public ApplicationFilter getFilterForCondition(FilterCondition condition) {
		for(ApplicationFilter f: filters) {
			if(f.getCondition().getIps().containsAll(condition.getIps()) ||
			   f.getCondition().getBrowser().equals(condition.getBrowser()) ||
			   f.getCondition().getOperatingSystem().equals(condition.getOperatingSystem())) {
				
				return f;
			}
		}
		
		return null;
	}
	
}
