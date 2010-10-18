package org.cssc.prototpe.testing.selectors;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Set;

public class SelectorServer {

	private Selector selector;
	private ServerSocketChannel serverSocket;
	private Thread listeningThread;
	private Thread processingThread;

	public SelectorServer(int port) {
		try {
			this.serverSocket = ServerSocketChannel.open();
			serverSocket.socket().bind(new InetSocketAddress(InetAddress.getLocalHost(), port));
			selector = Selector.open();
			listeningThread = new Thread(new ClientConnectionAccepter());
			listeningThread.start();
			processingThread = new Thread(new SocketProcessor());
			processingThread.start();
		} catch(IOException e) {
			System.out.println("Could not start the server.");
		}
	}

	private class ClientConnectionAccepter implements Runnable {
		public void run() {
			boolean finished = false;
			while(!finished) {
				try {
					SocketChannel s = serverSocket.accept();
					System.out.println("ConnectionAccepter - Accepted connection: " + s.socket().getRemoteSocketAddress());
					s.configureBlocking(false);
					s.register(selector, SelectionKey.OP_READ);
				} catch (IOException e) {
					finished = true;
				}
			}
		}
	}


	private class SocketProcessor implements Runnable {
		public void run() {
			try {
				selector.selectNow();
				Set<SelectionKey> keySet = selector.keys();

//				for(SelectionKey key: keySet) {
//					SocketChannel socket = null;
//					key.attach(socket);
//
//					System.out.println(socket.socket().getRemoteSocketAddress());
//				}
			} catch(IOException e) {
				System.out.println("Error.");
			}
		}
	}
	
	public static void main(String[] args) {
		new SelectorServer(8080);
		System.out.println("Started selector server...");
	}
}
