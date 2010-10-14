package org.cssc.prototpe.testing;

import java.io.IOException;
import java.net.Socket;

import org.cssc.prototpe.net.interfaces.ClientHandler;

public class EchoHandler implements ClientHandler {
	
	private int timeout;
	private byte[] buffer;
	
	public EchoHandler(int timeout, int bufsize) {
		this.timeout = timeout;
		this.buffer = new byte[bufsize];
	}

	@Override
	public void handle(Socket socket) {
		try {
			socket.setSoTimeout(timeout);
			while(true) {
				int bytesRead = socket.getInputStream().read(buffer);
				print(buffer, bytesRead);
				socket.getOutputStream().write(buffer, 0, bytesRead);
			}
		} catch(IOException e) {
			try {
				socket.close();
			} catch(IOException e1) {}
		}
	}
	
	private void print(byte[] buffer, int bytesRead){
		byte last = 0;
		
		for( int i = 0; i < bytesRead && buffer[i] != 0; i++){
			if( last == 13 && buffer[i] == 10){
				System.out.println("");
				break;
			}
			last = buffer[i];
			System.out.print((char)buffer[i]);
		}
	}

}
