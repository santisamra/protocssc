package org.cssc.prototpe.net;

public class ApplicationConfiguration {
	
	private int threadPoolSize;
	private String loggingFileName;

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
