package org.cssc.prototpe.net.monitoring;

import java.io.IOException;
import java.io.OutputStream;

import org.cssc.prototpe.net.Application;

public class MonitoredOutputStream extends OutputStream {
	
	private OutputStream wrapped;
	private boolean isClientOutputStream;
	private MonitoringService service;

	public MonitoredOutputStream(OutputStream wrapped, boolean isClientOutputStream) {
		this.wrapped = wrapped;
		this.isClientOutputStream = isClientOutputStream;
		this.service = Application.getInstance().getMonitoringService();
	}
	
	@Override
	public void write(int b) throws IOException {
		wrapped.write(b);
		if(isClientOutputStream) {
			service.addClientSentTransferredBytes(1);
		} else {
			service.addServerSentTransferredBytes(1);
		}
	}
	
	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		wrapped.write(b, off, len);
		if(isClientOutputStream) {
			service.addClientSentTransferredBytes(Math.min(b.length, len));
		} else {
			service.addServerSentTransferredBytes(Math.min(b.length, len));
		}
	}
	

}
