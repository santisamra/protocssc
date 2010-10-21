package org.cssc.prototpe.net.filters;

import java.io.IOException;
import java.net.Socket;

import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.filters.application.ApplicationFilter;

public class SocketFilter extends Filter {
	
	public SocketFilter(Socket clientSocket) {
		super(clientSocket);
	}

	public boolean filter(Socket clientSocket) throws IOException {
		ApplicationFilter filter = Application.getInstance().getApplicationConfiguration().getFilterForCondition(clientSocket.getInetAddress(), null);
		
		if(filter != null) {
			return applySocketFilter(filter);
		}
		
		return false;
	}
	

	protected boolean applySocketFilter(ApplicationFilter filter) throws IOException {
		if(filter.isAllAccessesBlocked()) {
			writeResponse("src/main/resources/html/errors/accessDenied.html");
			clientSocket.close();
			return true;
		}
		
		return false;
	}
}
