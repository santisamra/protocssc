package org.cssc.prototpe.net.filters;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

import org.cssc.prototpe.net.Application;

public class SocketFilter extends Filter {
	
	public SocketFilter(Socket clientSocket) {
		super(clientSocket);
	}

	public boolean filter(Socket clientSocket) throws IOException {
		List<InetAddress> ips = new LinkedList<InetAddress>();
		ips.add(clientSocket.getInetAddress());
		System.out.println(clientSocket.getInetAddress());
		
		FilterCondition condition = new FilterCondition(ips, null, null);
		ApplicationFilter filter = Application.getInstance().getApplicationConfiguration().getFilterForCondition(condition);
		
		return applyFilter(filter);
	}
}
