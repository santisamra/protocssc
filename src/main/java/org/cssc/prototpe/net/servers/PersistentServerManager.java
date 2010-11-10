package org.cssc.prototpe.net.servers;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.cssc.prototpe.net.exceptions.FatalException;
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
		synchronized(socketMap) {
			ensureUnused(addr);
			s = socketMap.get(addr);
			if(s != null) {
				socketList.remove(s);
			}
			if(s == null || s.isClosed() || !s.isConnected()) {
				// Must create a new connection
				if(s == null) {
					usedSocks++;
				}
				s = new Socket(addr, port);
				socketMap.put(addr, s);
			}
			socketList.add(0, s);
			usageAmount.put(addr, 1);
			if(usedSocks > maximumOpenSockets) {
				closeOldestSocket();
				usedSocks--;
			}
		}
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
				} catch (InterruptedException e) {
					throw new FatalException(e);
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
			// Do nothing
		}
		socketList.remove(socketList.size() - 1);
	}

	public void finishedRequest(Socket socket) {
		if(socket != null) {
			synchronized(socketMap) {
				usageAmount.put(socket.getInetAddress(), 0);
				socketMap.notifyAll();
			}
		}
	}

	@Override
	public Socket getEmergencySocket(InetAddress addr, int port)
			throws IOException {
		return new Socket(addr, port);
	}
}
