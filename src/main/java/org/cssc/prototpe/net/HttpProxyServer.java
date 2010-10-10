package org.cssc.prototpe.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Deque;
import java.util.LinkedList;

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
				Socket s;
				synchronized(clientConnections) {
					s = clientConnections.poll();
				}
				if(s != null) {
					try {
						if(!s.isClosed()) {
							InputStream inputStream = s.getInputStream();
							int availableBytes = inputStream.available(); 
							if(availableBytes > 0) {
								OutputStream outputStream = s.getOutputStream();
								inputStream.read(buffer, 0, availableBytes);
								System.out.println("SocketProcessor - Received from " + s.getRemoteSocketAddress() + ": " + buffer.toString());
								print(buffer);
								outputStream.write(buffer, 0, availableBytes);
								outputStream.flush();
							}
							synchronized(clientConnections){
								clientConnections.addLast(s);
							}
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
	
	private void print(byte[] buffer){
		byte last = 0;
		
		for( int i = 0; i < buffer.length && buffer[i] != 0; i++){
			if( last == 13 && buffer[i] == 10){
				System.out.println("");
				break;
			}
			last = buffer[i];
			System.out.print((char)buffer[i]);
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
		new HttpProxyServer(8080);
		System.out.println("Started echoing server...");
	}

}
