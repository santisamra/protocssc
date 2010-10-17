package org.cssc.prototpe.net;



/**
 * The main class for the application. 
 */
public class Application {
	
	private static Application instance = null;
	private ApplicationConfiguration applicationConfiguration;
	private ClientListener httpListener;
	private Logger logger;
	
	private static final int MAX_THREAD_COUNT = 50;
	private static final String LOGGING_FILE_NAME = "log.txt";
	
	private Application() {
		// This must be first, before anything else, as other parts of the
		// application require this instance.
		instance = this;
		
		// This MUST be second as other parts of the application require this configuration.
		applicationConfiguration = new ApplicationConfiguration();
		applicationConfiguration.setThreadPoolSize(MAX_THREAD_COUNT);
		applicationConfiguration.setLoggingFileName(LOGGING_FILE_NAME);
		
		// TODO: Place here any parts of the application that are needed by other parts.
		logger = new Logger(applicationConfiguration.getLoggingFileName());
		
		// This must be last, as it requires a configuration.
		httpListener = new HttpProxyClientListener(80);
	}
	
	public ApplicationConfiguration getApplicationConfiguration() {
		return applicationConfiguration;
	}

	public Logger getLogger() {
		return logger;
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
