package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import org.cssc.prototpe.net.exceptions.FatalException;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.net.interfaces.ConnectionManager;

public abstract class ClientListener {
	
	private ServerSocket serverSocket;
	private ConnectionManager connectionManager;
	
	protected abstract ClientHandler getHandler();
	
	protected ClientListener(int port, int backlog, InetAddress bindAddr) {
		try {
			serverSocket = new ServerSocket(port, backlog, bindAddr);
			connectionManager = ThreadPoolConnectionManager.getInstance();
		} catch (IOException e) {
			throw new FatalException(e);
		}
	}

	public ServerSocket getServerSocket() {
		return serverSocket;
	}
	
	/**
	 * Causes the CURRENT THREAD to start listening for connections.
	 * Once a connection is detected, it is automatically accepted.
	 */
	public void listen() {
		while(!Thread.interrupted()) {
			Socket newSocket;
			try {
				newSocket = getServerSocket().accept();
			} catch (IOException e) {
				throw new FatalException(e);
			}
			connectionManager.manage(newSocket, getHandler());
		}
	}

}
