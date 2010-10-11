package org.cssc.prototpe.testing;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

public class ThreadedEchoSelectServer {

	public static void main(String[] args) throws IOException {
		new ThreadedEchoSelectServer(8080);
	}

	private ServerSocketChannel server;
	private Deque<SocketChannel> temporaryChannels;
	private Selector acceptingSelector;
	private Selector readingSelector;
	private Thread acceptingThread;
	private Thread readingThread;

	public ThreadedEchoSelectServer(int port) throws IOException{
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.socket().bind(new InetSocketAddress(port));
		acceptingSelector = Selector.open();
		readingSelector = Selector.open();
		temporaryChannels = new LinkedList<SocketChannel>();
		server.register(acceptingSelector, SelectionKey.OP_ACCEPT);
		acceptingThread = new Thread(new ClientConnectionAccepter());
		readingThread = new Thread(new ClientConnectionReader());
		acceptingThread.start();
		readingThread.start();
		new Thread(new ClientConnectionWriter()).start();
	}

	private class ClientConnectionAccepter implements Runnable{
		@Override
		public void run() {
			boolean done = false;
			while(!done){
				try {
					Set<SelectionKey> selectedKeys;
					acceptingSelector.select();
					selectedKeys = acceptingSelector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					while(it.hasNext()){
						SelectionKey key = it.next();
						if(key.isAcceptable()){
							//							synchronized(ThreadedEchoSelectServer.this) {
							//								if(temporaryChannel != null) {
							//									ThreadedEchoSelectServer.this.wait();
							//								}
							//							}
							SocketChannel client = server.accept();
							client.configureBlocking(false);
							temporaryChannels.offer(client);
							System.out.println("Waking up.");
							readingSelector.wakeup();
						}
					}

				} catch (IOException e) {
					e.printStackTrace();
					done = true;
				}
			}
		}
	}

	private class ClientConnectionReader implements Runnable{
		@Override
		public void run() {
			boolean done = false;
			while(!done){
				try{
					Set<SelectionKey> selectedKeys;
					System.out.println("Starting select...");
					readingSelector.select();
					System.out.println("Ending select!");
					synchronized(ThreadedEchoSelectServer.this) {
						if(!temporaryChannels.isEmpty()) {
							temporaryChannels.poll().register(readingSelector, SelectionKey.OP_READ);
							//							ThreadedEchoSelectServer.this.notifyAll();
						}
					}
					selectedKeys = readingSelector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					while(it.hasNext()){
						SelectionKey key = it.next();
						if(key.isReadable()){
							it.remove();
							SocketChannel client = (SocketChannel) key.channel();
							client.configureBlocking(false);
							ByteBuffer buffer = ByteBuffer.allocate(100);
							client.read(buffer);
							print(buffer.array());
							if( buffer.remaining() < 100 ){
								System.out.println("Hay available");
								//									client.register(selector, SelectionKey.OP_WRITE, buffer);
							} else {
								System.out.println("No hay available");
								client.keyFor(readingSelector).cancel();
								client.close();
							}
						}
					}
				} catch (IOException e){
					e.printStackTrace();
					done = true;
				}
			}
		}
	}

	private class ClientConnectionWriter implements Runnable{
		@Override
		public void run() {
			try{
				Set<SelectionKey> selectedKeys;
				synchronized(acceptingSelector){
					selectedKeys = acceptingSelector.selectedKeys();
				}
				Iterator<SelectionKey> it = selectedKeys.iterator();
				while(it.hasNext()){
					SelectionKey key = it.next();
					if(key.isWritable()){
						it.remove();
						SocketChannel client = (SocketChannel) key.channel();
						ByteBuffer output = (ByteBuffer) key.attachment();
						output.rewind();
						print(output.array());
						client.write(output);
						synchronized(acceptingSelector){
							client.register(acceptingSelector, SelectionKey.OP_READ);
						}
					}
				}
			} catch (IOException e){
				e.printStackTrace();
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
}
