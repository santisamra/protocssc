package org.cssc.prototpe.net.clients;

import java.net.Socket;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.interfaces.ClientHandler;
import org.cssc.prototpe.net.interfaces.ConnectionManager;

public class ThreadPoolConnectionManager implements ConnectionManager {
	
	private static class SocketHandlerPair {
		
		private Socket socket;
		private ClientHandler handler;
		
		private SocketHandlerPair(Socket socket, ClientHandler handler) {
			this.socket = socket;
			this.handler = handler;
		}

		private Socket getSocket() {
			return socket;
		}

		private ClientHandler getHandler() {
			return handler;
		}
		
	}
	
	private static ThreadPoolConnectionManager instance = null;
	private int threadAmount;
	private Queue<SocketHandlerPair> taskQueue;
	private Semaphore tasksRemaining;
	private Thread[] threadArray;
	
	protected ThreadPoolConnectionManager() {
		this.threadAmount = Application.getInstance().getApplicationConfiguration().getThreadPoolSize();
		this.taskQueue = new LinkedList<SocketHandlerPair>();
		this.tasksRemaining = new Semaphore(0);
		generateThreads();
	}
	
	private void generateThreads() {
		int threadAmt = getThreadAmount();
		this.threadArray = new Thread[threadAmt];
		int i;
		for(i = 0; i < threadAmt; i++) {
			this.threadArray[i] = new Thread(new ThreadPoolRunnable());
			this.threadArray[i].start();
		}
	}
	
	protected int getThreadAmount() {
		return threadAmount;
	}
	
	private class ThreadPoolRunnable implements Runnable {
		
		private ThreadPoolRunnable() {}

		@Override
		public void run() {
			while(!Thread.interrupted()) {
				try {
					tasksRemaining.acquire();
					SocketHandlerPair pair;
					synchronized(taskQueue) {
						pair = taskQueue.poll();
					}
					pair.getHandler().handle(pair.getSocket());
				} catch (InterruptedException e) {
					return;
				}
			}
		}
		
	}

	@Override
	public void manage(Socket socket, ClientHandler handler) {
		taskQueue.add(new SocketHandlerPair(socket, handler));
		tasksRemaining.release();
	}
	
	public static ThreadPoolConnectionManager getInstance() {
		if(instance == null) {
			synchronized(ThreadPoolConnectionManager.class) {
				if(instance == null) {
					instance = new ThreadPoolConnectionManager();
				}
			}
		}
		return instance;
	}

}
