package org.cssc.prototpe.net.filters;

import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.cssc.prototpe.net.Application;

public class SocketFilter extends Filter {
	
	public SocketFilter(Socket clientSocket) {
		super(clientSocket);
	}

	public boolean filter(Socket clientSocket) {
		List<InetAddress> ips = new LinkedList<InetAddress>();
		ips.add(clientSocket.getInetAddress());
		
		FilterCondition condition = new FilterCondition(ips, "", "");
		ApplicationFilter filter = Application.getInstance().getApplicationConfiguration().getFilterForCondition(condition);
		
		return applyFilter(filter);
	}
}
