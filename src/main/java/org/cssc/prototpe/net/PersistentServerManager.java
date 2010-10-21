package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cssc.prototpe.net.interfaces.ServerManager;

/**
 * This class is in charge of handling the persistant connections with
 * the servers.
 */
public class PersistentServerManager implements ServerManager {
	
	private Map<InetAddress, Socket> socketMap;
	private Map<InetAddress, Integer> usageAmount;
	private List<Socket> socketList;
	private int usedSocks;
	private int maximumOpenSockets;
	
	public PersistentServerManager(int maximumOpenSockets) {
		this.socketMap = new HashMap<InetAddress, Socket>();
		this.usageAmount = new HashMap<InetAddress, Integer>();
		this.usedSocks = 0;
		this.maximumOpenSockets = maximumOpenSockets;
		this.socketList = new LinkedList<Socket>();
	}
	
	public Socket getSocket(InetAddress addr, int port) throws IOException {
		Socket s;
		System.out.println("Getting socket for address " + addr);
		synchronized(socketMap) {
			System.out.println("Entered synchronized block");
			ensureUnused(addr);
			System.out.println("Ensured unused, obtaining tentative socket");
			s = socketMap.get(addr);
			System.out.println("Obtained tentative socket, ensuring it's not closed");
			if(s == null || s.isClosed() || !s.isConnected()) {
				// Must create a new connection
				System.out.println("Creating a new connection for " + addr);
				s = new Socket(addr, port);
				socketMap.put(addr, s);
				usedSocks++;
				if(usedSocks > maximumOpenSockets) {
					closeOldestSocket();
					usedSocks--;
				}
			}
			socketList.remove(s);
			socketList.add(0, s);
			usageAmount.put(addr, 1);
		}
		System.out.println("Exiting block");
		return s;
	}

	private void ensureUnused(InetAddress addr) {
		Integer usages;
		boolean done = false;
		while(!done) {
			usages = usageAmount.get(addr);
			if(usages != null && usages > 0) {
				try {
					socketMap.wait();
					System.out.println("a");
				} catch (InterruptedException e) {
					//TODO: what to do here?
				}
			} else {
				done = true;
			}
		}
	}
	
	private void closeOldestSocket() {
		Socket oldestSocket = socketList.get(socketList.size() - 1);
		ensureUnused(oldestSocket.getInetAddress());
		try {
			oldestSocket.close();
		} catch (IOException e) {
			//TODO: what to do here?
			System.err.println(oldestSocket);
			e.printStackTrace();
		}
	}

	public void finishedRequest(Socket socket) {
		System.out.println("Closing " + socket.getInetAddress());
		synchronized(socketMap) {
			usageAmount.put(socket.getInetAddress(), 0);
			socketMap.notifyAll();
		}
	}
}