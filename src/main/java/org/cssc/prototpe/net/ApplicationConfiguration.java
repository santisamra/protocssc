package org.cssc.prototpe.net;

public class ApplicationConfiguration {
	
	private int threadPoolSize;
	private int maxPersistantServerConnections;
	private String loggingFileName;
	
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
	
}
