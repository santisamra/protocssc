package org.cssc.prototpe.testing;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class ProxySelectTest {
	
	public static void main(String[] args) {
		try {
			new ProxySelectTest(8080);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ProxySelectTest(int port) throws IOException{
		ServerSocketChannel server = ServerSocketChannel.open();
	    server.configureBlocking(false);

	    server.socket().bind(new InetSocketAddress(port));
	    Selector selector = Selector.open();
	    server.register(selector, SelectionKey.OP_ACCEPT);

	    while (true) {
	      selector.select();
	      Set<SelectionKey> readyKeys = selector.selectedKeys();
	      Iterator<SelectionKey> iterator = readyKeys.iterator();
	      while (iterator.hasNext()) {
	        SelectionKey key = (SelectionKey) iterator.next();
	        iterator.remove();
	        if (key.isAcceptable()) {
	          SocketChannel client = server.accept();
	          System.out.println("Accepted connection from " + client);
	          client.configureBlocking(false);
	          client.register(selector, SelectionKey.OP_READ);
	          
	        } else if (key.isReadable()){
	        	SocketChannel client = (SocketChannel) key.channel();
	        	client.configureBlocking(false);
	        	
	        	InputStream stream = client.socket().getInputStream();
	        	if( stream.available() > 0){
	        		byte[] buf = new byte[stream.available() + 1];
	        		stream.read(buf, 0, stream.available());
	        		ByteBuffer source = ByteBuffer.wrap(buf);
	        		SelectionKey key2 = client.register(selector, SelectionKey.OP_WRITE);
	        		key2.attach(source);
	        	} else {
	        		client.register(selector, SelectionKey.OP_READ);
	        	}
	        	
	        	
	        } else if (key.isWritable()) {
	          SocketChannel client = (SocketChannel) key.channel();
	          ByteBuffer output = (ByteBuffer) key.attachment();
	          if (!output.hasRemaining()) {
	            output.rewind();
	          }
	          client.write(output);
	          client.register(selector, SelectionKey.OP_READ);
	        }
//	        key.channel().close();
	      }
	    }
	}
	
	
}
