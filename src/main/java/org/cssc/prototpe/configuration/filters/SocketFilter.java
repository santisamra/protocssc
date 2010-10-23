package org.cssc.prototpe.configuration.filters;

import java.io.IOException;
import java.net.Socket;

import org.cssc.prototpe.configuration.filters.application.ApplicationFilter;
import org.cssc.prototpe.net.Application;

public class SocketFilter extends Filter {
	
	public SocketFilter(Socket clientSocket) {
		super(clientSocket);
	}

	public boolean filter() throws IOException {
		ApplicationFilter filter = Application.getInstance().getApplicationConfiguration().getFilterForCondition(clientSocket.getInetAddress(), null);
		
		if(filter != null) {
			return applyFilter(filter);
		}
		
		return false;
	}
	

	private boolean applyFilter(ApplicationFilter filter) throws IOException {
		if(filter.isAllAccessesBlocked()) {
			writeResponse("src/main/resources/html/errors/accessDenied.html");
			return true;
		}
		
		return false;
	}
}
