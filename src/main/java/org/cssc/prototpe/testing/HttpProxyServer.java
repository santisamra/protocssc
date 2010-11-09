package org.cssc.prototpe.testing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.LinkedList;

import org.cssc.prototpe.net.exceptions.FatalException;

public class HttpProxyServer {

	private ServerSocketChannel serverSocket;
	private Thread listeningThread;
	private Thread processingThread;
	private Deque<SocketChannel> clientConnections;

	private class ClientConnectionAccepter implements Runnable {
		@Override
		public void run() {
			boolean finished = false;
			while(!finished) {
				try {
					SocketChannel s = serverSocket.accept();
					System.out.println("ConnectionAccepter - Accepted connection: " + s.socket().getRemoteSocketAddress());
					s.configureBlocking(false);
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

		private static final int INPUT_BUFFER_SIZE = 1024; //1KB

		@Override
		public void run() {
			boolean finished = false;
//			byte[] buffer = new byte[INPUT_BUFFER_SIZE];
			ByteBuffer buffer = ByteBuffer.allocateDirect(INPUT_BUFFER_SIZE);
			while(!finished) {
				SocketChannel s;
				synchronized(clientConnections) {
					s = clientConnections.poll();
				}
				if(s != null) {
					try {
						if(s.isConnected()) {
//							InputStream inputStream = s.getInputStream();
//							OutputStream outputStream = s.getOutputStream();
//							int availableBytes = inputStream.available();
							
//							if(availableBytes > 0) {
//								inputStream.read(buffer, 0, 1000);
//								System.out.println("SocketProcessor - Received from " + s.getRemoteSocketAddress() + ": ");
//								print(buffer);
//								outputStream.write(buffer, 0, availableBytes);
//								outputStream.flush();
//							} else {
//								outputStream.write(0);
//							}
							
//							System.out.println(s.isBlocking());
							long availableBytes = s.read(buffer);
							if(availableBytes > 0) {
								System.out.println("SocketProcessor - Received: " + availableBytes);
								System.out.println("Sending: " + buffer.remaining());
								s.write(buffer);
							}
							
							synchronized(clientConnections){
								clientConnections.addLast(s);
							}
						} else {
							System.out.println("Connection closed");
							s.close();
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
	


	public HttpProxyServer(int port) {
		try {
			this.serverSocket = ServerSocketChannel.open();
			serverSocket.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), 8080));
		} catch (IOException e) {
			throw new FatalException(e);
		}
		this.clientConnections = new LinkedList<SocketChannel>();
		listeningThread = new Thread(new ClientConnectionAccepter());
		listeningThread.start();
		processingThread = new Thread(new SocketProcessor());
		processingThread.start();
	}

	public static void main(String[] args) {
		new HttpProxyServer(8080);
		System.out.println("Started echoing server...");
	}

}
