package org.cssc.prototpe.net;

import org.cssc.prototpe.testing.EchoTestClientListener;


/**
 * The main class for the application. 
 */
public class Application {
	
	private static Application instance = null;
	private ApplicationConfiguration applicationConfiguration;
	private ClientListener httpListener;
	
	private static final int MAX_THREAD_COUNT = 2;
	
	private Application() {
		applicationConfiguration = new ApplicationConfiguration();
		applicationConfiguration.setThreadPoolSize(MAX_THREAD_COUNT);
		httpListener = new EchoTestClientListener(8080);
	}
	
	public ApplicationConfiguration getApplicationConfiguration() {
		return applicationConfiguration;
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
