package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Semaphore;

import org.cssc.prototpe.net.interfaces.ServerManager;

public class PersistentSemaphorizedServerManager implements ServerManager {
	
	private Map<InetAddress, Semaphore> semaphoreMap;
	private Map<InetAddress, Queue<Socket>> freeSockets;
	private List<Queue<Socket>> oldestUsedQueues;
	private Semaphore totalSockets;
	private int usedSockets;
	private int maxSockets;
	private int maxSocketsPerServer;

	public PersistentSemaphorizedServerManager(int maxSockets, int maxSocketsPerServer) {
		this.semaphoreMap = new HashMap<InetAddress, Semaphore>();
		this.freeSockets = new HashMap<InetAddress, Queue<Socket>>();
		this.maxSockets = maxSockets;
		this.usedSockets = 0;
		this.maxSocketsPerServer = maxSocketsPerServer;
		this.totalSockets = new Semaphore(maxSockets);
		this.oldestUsedQueues = new LinkedList<Queue<Socket>>();
	}
	
	@Override
	public Socket getSocket(InetAddress addr, int port) throws IOException {
		Semaphore semaphore;
		semaphore = getSemaphore(addr);
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
			Queue<Socket> freeSocketQueue = freeSockets.get(addr);
			if(freeSocketQueue == null) {
				freeSocketQueue = new LinkedList<Socket>();
				freeSockets.put(addr, freeSocketQueue);
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

	private Semaphore getSemaphore(InetAddress addr) {
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
		Semaphore semaphore = getSemaphore(addr);
		synchronized(freeSockets) {
			if(!socket.isClosed()) {
				Queue<Socket> freeSocketQueue = freeSockets.get(addr);
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
