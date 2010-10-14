package org.cssc.prototpe.net;

import java.net.Socket;

import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.net.interfaces.ConnectionManager;

public class ThreadedConnectionManager implements ConnectionManager {
	
	private static ThreadedConnectionManager instance = null;
	
	protected ThreadedConnectionManager() {}
	
	private static class HandlingRunnable implements Runnable {
		
		private Socket socket;
		private ClientHandler handler;
		
		private HandlingRunnable(Socket socket, ClientHandler handler) {
			this.socket = socket;
			this.handler = handler;
		}

		@Override
		public void run() {
			handler.handle(socket);
		}
		
	}

	@Override
	public void manage(Socket socket, ClientHandler handler) {
		Thread t = new Thread(new HandlingRunnable(socket, handler));
		t.start();
	}
	
	public static ThreadedConnectionManager getInstance() {
		if(instance == null) {
			synchronized(ThreadedConnectionManager.class) {
				if(instance == null) {
					instance = new ThreadedConnectionManager();
				}
			}
		}
		return instance;
	}

}
