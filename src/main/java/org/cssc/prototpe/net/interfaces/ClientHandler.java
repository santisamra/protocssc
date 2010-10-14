package org.cssc.prototpe.net.interfaces;

import java.net.Socket;


/**
 * This interface represents a handler for a specific client.
 * Given a socket which should be initially connected (although this is not guaranteed), 
 * it is supposed to handle the connection between a certain client and
 * the server, and execute all the operations that are needed. 
 */
public interface ClientHandler {
	
	public void handle(Socket socket);

}
