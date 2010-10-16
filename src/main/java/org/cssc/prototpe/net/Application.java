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
		// This must be first, before anything else, as other parts of the
		// application require this instance.
		instance = this;
		
		// This MUST be second as other parts of the application require this configuration.
		applicationConfiguration = new ApplicationConfiguration();
		applicationConfiguration.setThreadPoolSize(MAX_THREAD_COUNT);
		
		// TODO: Place here any parts of the application that are needed by other parts.
		
		// This must be last, as it requires a configuration.
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
		return instance;
	}

}
