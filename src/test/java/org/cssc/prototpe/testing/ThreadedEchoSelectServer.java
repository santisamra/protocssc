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
	private Deque<SocketChannel> channelsToRead;
	private Deque<SocketChannel> channelsToWrite;

	// Ver si se puede mejorar esto.
	private Deque<ByteBuffer> writingBuffers;

	private Selector acceptingSelector;
	private Selector readingSelector;
	private Selector writingSelector;
	private Thread acceptingThread;
	private Thread readingThread;

	public ThreadedEchoSelectServer(int port) throws IOException{
		server = ServerSocketChannel.open();
		server.configureBlocking(false);
		server.socket().bind(new InetSocketAddress(port));
		acceptingSelector = Selector.open();
		readingSelector = Selector.open();
		writingSelector = Selector.open();
		channelsToRead = new LinkedList<SocketChannel>();
		channelsToWrite = new LinkedList<SocketChannel>();
		writingBuffers = new LinkedList<ByteBuffer>();
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
							channelsToRead.offer(client);
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
					System.out.println("Starting reading select...");
					readingSelector.select();
					System.out.println("Ending reading select!");
					synchronized(ThreadedEchoSelectServer.this) {
						while(!channelsToRead.isEmpty()) {
							channelsToRead.poll().register(readingSelector, SelectionKey.OP_READ);
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
								channelsToWrite.offer(client);
								writingBuffers.offer(buffer);
								writingSelector.wakeup();
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
			boolean done = false;
			while(!done) {
				try{
					Set<SelectionKey> selectedKeys;
					System.out.println("Starting writing select...");
					writingSelector.select();
					System.out.println("Ending writing select!");

					synchronized(ThreadedEchoSelectServer.this) {
						while(!channelsToWrite.isEmpty()) {
							channelsToWrite.poll().register(writingSelector, SelectionKey.OP_WRITE, writingBuffers.poll());
						}
					}

					selectedKeys = writingSelector.selectedKeys();
					Iterator<SelectionKey> it = selectedKeys.iterator();
					while(it.hasNext()){
						SelectionKey key = it.next();
						if(key.isWritable()){
							it.remove();
							SocketChannel client = (SocketChannel) key.channel();
							ByteBuffer output = (ByteBuffer) key.attachment();
							output.rewind();
							client.write(output);
							key.cancel();
						}
					}
				} catch (IOException e){
					e.printStackTrace();
					done = true;
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
}
