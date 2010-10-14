package org.cssc.prototpe.net.interfaces;

import java.net.Socket;


/**
 * This interface defines the way in which connections are handled.
 * A Socket that represents a connection that has just been made
 * is given to the manage method.
 */
public interface ConnectionManager {
	
	/**
	 * This method is in charge of starting
	 * any threads, or doing anything that is needed to manage it.
	 * This method MUST return as soon as possible, because it will be
	 * directly called by the same thread that is accepting connections.
	 */
	public void manage(Socket socket, ClientHandler handler);

}
