package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.cssc.prototpe.net.interfaces.ServerManager;

public class PersistentSemaphorizedServerManager implements ServerManager {
	
	private Map<InetSocketAddress, Semaphore> semaphoreMap;
	private Map<InetSocketAddress, Queue<Socket>> freeSockets;
	private List<Queue<Socket>> oldestUsedQueues;
	private Semaphore totalSockets;
	private int usedSockets;
	private int maxSockets;
	private int maxSocketsPerServer;

	public PersistentSemaphorizedServerManager(int maxSockets, int maxSocketsPerServer) {
		this.semaphoreMap = new HashMap<InetSocketAddress, Semaphore>();
		this.freeSockets = new HashMap<InetSocketAddress, Queue<Socket>>();
		this.maxSockets = maxSockets;
		this.usedSockets = 0;
		this.maxSocketsPerServer = maxSocketsPerServer;
		this.totalSockets = new Semaphore(maxSockets);
		this.oldestUsedQueues = new LinkedList<Queue<Socket>>();
	}
	
	@Override
	public Socket getSocket(InetAddress addr, int port) throws IOException {
		InetSocketAddress address = new InetSocketAddress(addr, port);
		Semaphore semaphore;
		semaphore = getSemaphore(address);
		try {
			semaphore.acquire();
		} catch(InterruptedException e) {
			//TODO: what to do here?
		}
		Socket s;
		try {
			totalSockets.acquire();
		} catch(InterruptedException e) {
			//TODO: what to do here?
		}
		synchronized(freeSockets) {
			Queue<Socket> freeSocketQueue = freeSockets.get(address);
			if(freeSocketQueue == null) {
				freeSocketQueue = new LinkedList<Socket>();
				freeSockets.put(address, freeSocketQueue);
			}
			this.oldestUsedQueues.remove(freeSocketQueue);
			this.oldestUsedQueues.add(freeSocketQueue);
			s = freeSocketQueue.poll();
			if(s == null) {
				if(usedSockets >= maxSockets) {
					// I know there is at least 1 free Socket because
					// of the large Semaphore, and because one Socket may only
					// be used by 1 thread at the same time.
					closeOldestSocket();
				}
				s = new Socket(addr, port);
				usedSockets++;
				// Security measures, in case a naughty server doesn't answer
				s.setSoTimeout(Application.getInstance().getApplicationConfiguration().getServerConnectionPersistentTimeout());
			}
		}
		return s;
	}
	
	private void closeOldestSocket() {
		for(Queue<Socket> q : oldestUsedQueues) {
			if(!q.isEmpty()) {
				Socket s = q.poll();
				try {
					s.close();
				} catch(IOException e) {
					//TODO: what to do here?
				}
				usedSockets--;
				return;
			}
		}
	}

	private Semaphore getSemaphore(InetSocketAddress addr) {
		Semaphore semaphore;
		synchronized(semaphoreMap) {
			semaphore = semaphoreMap.get(addr);
			if(semaphore == null) {
				semaphore = new Semaphore(maxSocketsPerServer);
				semaphoreMap.put(addr, semaphore);
			}
		}
		return semaphore;
	}
	
	@Override
	public void finishedRequest(Socket socket) {
		InetAddress addr = socket.getInetAddress();
		InetSocketAddress address = new InetSocketAddress(addr, socket.getPort());
		Semaphore semaphore = getSemaphore(address);
		synchronized(freeSockets) {
			if(!socket.isClosed()) {
				Queue<Socket> freeSocketQueue = freeSockets.get(address);
				freeSocketQueue.offer(socket);
			} else {
				usedSockets--;
			}
		}
		totalSockets.release();
		semaphore.release();
	}

	@Override
	public Socket getEmergencySocket(InetAddress addr, int port)
			throws IOException {
		return new Socket(addr, port);
	}


}
