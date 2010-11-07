package org.cssc.prototpe.configuration.filters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.nio.charset.Charset;

import org.cssc.prototpe.configuration.filters.application.ApplicationFilter;
import org.cssc.prototpe.configuration.filters.exceptions.FilterException;
import org.cssc.prototpe.http.HttpHeader;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.http.HttpResponseCode;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.Logger;

public abstract class Filter {

	protected Socket clientSocket;
	protected ApplicationFilter filter;
	private Logger logger;

	public Filter(Socket clientSocket, ApplicationFilter filter, Logger logger) {
		if(clientSocket == null) {
			throw new IllegalArgumentException("Client socket cannot be null.");
		}
		
		this.clientSocket = clientSocket;
		this.filter = filter;
		this.logger = logger;
	}
	
	public abstract boolean filter() throws IOException;
	
	protected void writeResponse(String htmlResponsePath) {
		InputStream inputStream = null;

		try {
			inputStream = new FileInputStream(htmlResponsePath);
		} catch (FileNotFoundException e) {
			throw new FilterException(e);
		}

		int contentLength = 0;
		StringBuffer buffer = new StringBuffer();

		int readInt;

		try {
			while((readInt = inputStream.read()) != -1) {
				buffer.append((char)readInt);
				contentLength++;
			}

			HttpHeader header = new HttpHeader();
			header.setField("content-length", Integer.toString(contentLength));
			HttpResponse response = new HttpResponse("1.1", header, HttpResponseCode.FORBIDDEN, "FORBIDDEN", new byte[0]);
			byte[] bytes = response.toString().getBytes(Charset.forName("US-ASCII"));
			clientSocket.getOutputStream().write(bytes);
			Application.getInstance().getMonitoringService().addClientSentTransferredBytes(bytes.length);
			bytes = buffer.toString().getBytes(Charset.forName("US-ASCII"));
			clientSocket.getOutputStream().write(bytes);
			Application.getInstance().getMonitoringService().addClientSentTransferredBytes(bytes.length);

			logger.logFilterResponse(clientSocket.getInetAddress(), response);

		} catch (IOException e) {
			throw new FilterException(e);
		}

	}
}
