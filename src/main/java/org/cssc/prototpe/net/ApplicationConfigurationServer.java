package org.cssc.prototpe.net;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.cssc.prototpe.net.exceptions.FatalException;

public class ApplicationConfigurationServer implements Runnable{

	ServerSocket serverSocket;
	ApplicationConfiguration configuration;
	
	
	public ApplicationConfigurationServer(int port){
		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			throw new FatalException(e);
		}
		configuration = Application.getInstance().getApplicationConfiguration();
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		
		Socket socket;
		
		while(true){
			try {
				socket = serverSocket.accept();
				
				
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	
}
