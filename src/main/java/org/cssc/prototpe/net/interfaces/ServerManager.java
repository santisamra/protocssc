package org.cssc.prototpe.net.interfaces;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

public interface ServerManager {

	public Socket getSocket(InetAddress addr, int port) throws IOException;
	
	public void finishedRequest(Socket socket);
	
}
