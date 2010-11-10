package org.cssc.prototpe.net.monitoring;

import java.io.IOException;
import java.io.InputStream;

import org.cssc.prototpe.net.Application;

public class MonitoredInputStream extends InputStream {
	
	private InputStream wrapped;
	private boolean isClientInputStream;
	private MonitoringService service;

	public MonitoredInputStream(InputStream wrapped, boolean isClientInputStream) {
		this.wrapped = wrapped;
		this.isClientInputStream = isClientInputStream;
		this.service = Application.getInstance().getMonitoringService();
	}
	
	@Override
	public int read() throws IOException {
		int read = wrapped.read();
		if(read != -1) {
			if(isClientInputStream) {
				service.addClientReceivedTransferredBytes(1);
			} else {
				service.addServerReceivedTransferredBytes(1);
			}
		}
		return read;
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		int ret = wrapped.read(b);
		if(ret != -1) {
			if(isClientInputStream) {
				service.addClientReceivedTransferredBytes(ret);
			} else {
				service.addServerReceivedTransferredBytes(ret);
			}
		}
		return ret;
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		int ret = wrapped.read(b, off, len);
		if(ret != -1) {
			if(isClientInputStream) {
				service.addClientReceivedTransferredBytes(ret);
			} else {
				service.addServerReceivedTransferredBytes(ret);
			}
		}
		return ret;
	}

}
