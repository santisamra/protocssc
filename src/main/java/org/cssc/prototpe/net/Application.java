package org.cssc.prototpe.net;

import org.cssc.prototpe.testing.EchoTestClientListener;


/**
 * The main class for the application. 
 */
public class Application {
	
	private static Application instance = null;
	private ClientListener httpListener;
	
	private Application() {
		httpListener = new EchoTestClientListener(80);
	}
	
	/**
	 * The main method of the Application class. Executes the application.
	 */
	public void launch() {
		httpListener.listen();
	}
	
	public static void main(String[] args) {
		new Application().launch();
	}
	
	public static Application getInstance() {
		if(instance == null) {
			synchronized(Application.class) {
				if(instance == null) {
					instance = new Application();
				}
			}
		}
		return instance;
	}

}
