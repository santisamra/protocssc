package org.cssc.prototpe.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

public class HttpProxyServer {

	private ServerSocket serverSocket;
	private Thread listeningThread;
	private Thread processingThread;
	private Deque<Socket> clientConnections;

	private class ClientConnectionAccepter implements Runnable {
		@Override
		public void run() {
			boolean finished = false;
			while(!finished) {
				try {
					Socket s = serverSocket.accept();
					System.out.println("ConnectionAccepter - Accepted connection: " + s.getRemoteSocketAddress());
					synchronized(clientConnections) {
						clientConnections.addFirst(s);
					}
				} catch (IOException e) {
					finished = true;
				}
			}
		}
	}

	private class SocketProcessor implements Runnable {

		private static final int INPUT_BUFFER_SIZE = 1048576; //1MB

		@Override
		public void run() {
			boolean finished = false;
			byte[] buffer = new byte[INPUT_BUFFER_SIZE];
			while(!finished) {
				// FIXME: Major performance defect
				synchronized(clientConnections) {
					Socket s = clientConnections.poll();
					if(s != null) {
						try {
							if(!s.isClosed()) {
								InputStream inputStream = s.getInputStream();
								int availableBytes = inputStream.available(); 
								if(availableBytes > 0) {
									OutputStream outputStream = s.getOutputStream();
									inputStream.read(buffer, 0, availableBytes);
									System.out.println("SocketProcessor - Received from " + s.getRemoteSocketAddress() + ": " + buffer.toString());
									outputStream.write(buffer, 0, availableBytes);
									outputStream.flush();
								}
								clientConnections.addLast(s);
							}
						} catch(IOException e) {
							try {
								s.close();
								System.out.println("Closing a socket");
							} catch (IOException e1) {
								throw new FatalException(e1);
							}
						}
					}
				}
			}
		}
	}

	public HttpProxyServer(int port) {
		try {
			this.serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new FatalException(e);
		}
		this.clientConnections = new LinkedList<Socket>();
		listeningThread = new Thread(new ClientConnectionAccepter());
		listeningThread.start();
		processingThread = new Thread(new SocketProcessor());
		processingThread.start();
	}

	public static void main(String[] args) {
		HttpProxyServer server = new HttpProxyServer(8080);
	}

}
