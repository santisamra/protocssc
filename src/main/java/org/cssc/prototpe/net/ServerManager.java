package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is in charge of handling the persistant connections with
 * the servers.
 */
public class ServerManager {
	
	private static final int HTTP_PORT = 80;
	
	private Map<InetAddress, Socket> socketMap;
	private Map<InetAddress, Integer> usageAmount;
	
	public ServerManager() {
		this.socketMap = new HashMap<InetAddress, Socket>();
		this.usageAmount = new HashMap<InetAddress, Integer>();
	}
	
	public Socket getSocket(InetAddress addr) throws IOException {
		Socket s;
		synchronized(socketMap) {
			s = socketMap.get(addr);
			Integer usages = 1;
			boolean done = false;
			while(!done) {
				usages = usageAmount.get(addr);
				if(usages != null && usages > 0) {
					try {
						socketMap.wait();
					} catch (InterruptedException e) {
						//TODO: what to do here?
					}
				} else {
					done = true;
				}
			}
			if(s == null || s.isClosed() || !s.isConnected()) {
				// Must create a new connection
				s = new Socket(addr, HTTP_PORT);
				socketMap.put(addr, s);
			}
			usageAmount.put(addr, 1);
		}
		return s;
	}
	
	public void finishedRequest(InetAddress addr) {
		synchronized(socketMap) {
			usageAmount.put(addr, 0);
			socketMap.notifyAll();
		}
	}
}
