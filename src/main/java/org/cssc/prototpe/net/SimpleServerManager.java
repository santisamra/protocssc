package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import org.cssc.prototpe.net.interfaces.ServerManager;

public class SimpleServerManager implements ServerManager {

	
	@Override
	public void finishedRequest(Socket socket) {
		
	}

	@Override
	public Socket getSocket(InetAddress addr, int port) throws IOException {
		return new Socket(addr, port);
	}

	@Override
	public Socket getEmergencySocket(InetAddress addr, int port)
			throws IOException {
		return getSocket(addr, port);
	}

}
