package org.cssc.prototpe.net;

import java.io.File;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cssc.prototpe.configuration.ConfigurationParser;
import org.cssc.prototpe.configuration.filters.application.ApplicationFilter;

public class ApplicationConfiguration {
	
	private int proxyPort;
	private int remoteServicesPort;
	private int threadPoolSize;
	private int maxPersistantServerConnections;
	private int maxPersistantServerConnectionsPerServer;
	private String loggingFileName;
	private InetAddress chainingProxyAddress;
	private int chainingProxyPort;
	private int clientKeepAliveTimeout;
	private int serverConnectionPersistentTimeout;
	private List<ApplicationFilter> filters;
	private Map<String, String> adminUsers;
	
	public ApplicationConfiguration(String xmlString) {
		ConfigurationParser parser = new ConfigurationParser(xmlString);
		loadValues(parser);
	}
	
	public ApplicationConfiguration(File xmlFile) {
		ConfigurationParser parser = new ConfigurationParser(xmlFile);
		loadValues(parser);
	}
	
	private void loadValues(ConfigurationParser parser) {
		proxyPort = parser.getProxyPort();
		remoteServicesPort = parser.getRemoteServicesPort();
		threadPoolSize = parser.getThreadPoolSize();
		maxPersistantServerConnections = parser.getMaxPersistantServerConnections();
		maxPersistantServerConnectionsPerServer = parser.getMaxPersistantServerConnectionsPerServer();
		loggingFileName = parser.getLoggingFileName();
		chainingProxyAddress = parser.getChainingProxyAddress();
		chainingProxyPort = parser.getChainingProxyPort();
		clientKeepAliveTimeout = parser.getClientKeepAliveTimeout();
		serverConnectionPersistentTimeout = parser.getServerConnectionPersistentTimeout();
		filters = parser.getFilters();
		adminUsers = parser.getAdminUsers();
	}
	
	public int getProxyPort() {
		return proxyPort;
	}
	
	public int getRemoteServicesPort() {
		return remoteServicesPort;
	}
	
	public int getClientKeepAliveTimeout() {
		return clientKeepAliveTimeout;
	}

	public void setClientKeepAliveTimeout(int clientKeepAliveTimeout) {
		this.clientKeepAliveTimeout = clientKeepAliveTimeout;
	}
	
	public int getServerConnectionPersistentTimeout() {
		return serverConnectionPersistentTimeout;
	}
	
	public void setServerConnectionPersistentTimeout(int serverConnectionPersistentTimeout) {
		this.serverConnectionPersistentTimeout = serverConnectionPersistentTimeout;
	}

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
	
	public void setChainingProxy(InetAddress address, int port){
		this.chainingProxyAddress = address;
		this.chainingProxyPort = port;
	}
	
	public InetAddress getChainingProxyAddress(){
		return chainingProxyAddress;
	}
	
	public int getChainingProxyPort(){
		return chainingProxyPort;
	}
	
	public boolean isProxied(){
		return chainingProxyAddress != null && chainingProxyPort != 0;
	}
	
	public void addFilter(ApplicationFilter filter) {
		filters.add(filter);
	}
	
	public void removeFilter(ApplicationFilter filter) {
		filters.remove(filter);
	}
	
	public List<ApplicationFilter> getFilters() {
		return filters;
	}
	
	public void replaceAllFilters(List<ApplicationFilter> filters){
		this.filters = filters;
	}
	
	public ApplicationFilter getFilterForCondition(InetAddress ip, String userAgent) {
		if(ip == null) {
			throw new IllegalArgumentException("IP cannot be null.");
		}
		
		for(ApplicationFilter f: filters) {
			List<InetAddress> ips = f.getCondition().getIps();
			String browser = f.getCondition().getBrowser();
			String operatingSystem = f.getCondition().getOperatingSystem();
			
			boolean aux1 = false;
			boolean aux2 = false;
			boolean aux3 = false;
			
			if(ips != null && ips.size() > 0) {
				if(ips.contains(ip)) {
					aux1 = true;
				}
			} else {
				aux1 = true;
			}
			
			if(browser != null && !browser.equals("")) {
				if(userAgent == null || userAgent.contains(browser)) {
					aux2 = true;
				}
			} else {
				aux2 = true;
			}
			
			if(operatingSystem != null && !operatingSystem.equals("")) {
				if(userAgent == null || userAgent.contains(operatingSystem)) {
					aux3 = true;
				}
			} else {
				aux3 = true;
			}
			
			if(aux1 && aux2 && aux3) {
				return f;
			}
		}
		
		return null;
	}
	
	public int getMaxPersistantServerConnectionsPerServer() {
		return maxPersistantServerConnectionsPerServer;
	}

	public void setMaxPersistantServerConnectionsPerServer(
			int maxPersistantServerConnectionsPerServer) {
		this.maxPersistantServerConnectionsPerServer = maxPersistantServerConnectionsPerServer;
	}
	
	public Map<String, String> getAdminUsers(){
		return adminUsers;
	}
	
}
