package org.cssc.prototpe.net;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JOptionPane;

import org.cssc.prototpe.configuration.filters.application.ApplicationFilter;
import org.cssc.prototpe.configuration.filters.application.FilterCondition;
import org.cssc.prototpe.httpserver.ApplicationConfigurationServer;
import org.cssc.prototpe.net.interfaces.ServerManager;




/**
 * The main class for the application. 
 */
public class Application {

	private static Application instance = null;
	private ApplicationConfiguration applicationConfiguration;
	private ClientListener httpListener;
	private Logger logger;
	private ServerManager serverManager;

	private static final String CONFIG_FILE = "src/main/resources/config/config.xml";
	private static final int MAX_THREAD_COUNT = 50;
	private static final int MAX_PERSISTANT_SERVER_CONNECTIONS = 20;
	private static final int CLIENT_KEEP_ALIVE_TIMEOUT_MS = 30000;
	private static final String LOGGING_FILE_NAME = "log.txt";

	private Application() {
		// This must be first, before anything else, as other parts of the
		// application require this instance.
		instance = this;

		// This MUST be second as other parts of the application require this configuration.
		applicationConfiguration = new ApplicationConfiguration();
		applicationConfiguration.loadInitialValues(CONFIG_FILE);
		//TODO: this is a parche
		applicationConfiguration.setThreadPoolSize(MAX_THREAD_COUNT);
		applicationConfiguration.setLoggingFileName(LOGGING_FILE_NAME);
		applicationConfiguration.setMaxPersistantServerConnections(MAX_PERSISTANT_SERVER_CONNECTIONS);
		applicationConfiguration.setClientKeepAliveTimeout(CLIENT_KEEP_ALIVE_TIMEOUT_MS);


		// TODO: Place here any parts of the application that are needed by other parts.
		logger = new Logger(applicationConfiguration.getLoggingFileName());
//		serverManager = new SimpleServerManager();
		serverManager = new PersistentServerManager(applicationConfiguration.getMaxPersistantServerConnections());

		//TODO: Nada validado obviamente.. TESTING PURO
		int port; 

		try{
			port = Integer.valueOf(JOptionPane.showInputDialog("Port?"));
		}catch(NumberFormatException e){
			port = 8080;
		}

		System.out.println("Starting server at port " + port);
		String proxyIP = JOptionPane.showInputDialog("Proxy Chaining IP? Otherwise leave empty");
		int proxyport;
		try{
			proxyport = Integer.valueOf(JOptionPane.showInputDialog("Proxy Chaining Port? Otherwise leave empty"));
		} catch (NumberFormatException e){
			proxyport = 0;
		}
		try {
			if( proxyIP != null && proxyport != 0){
				System.out.println("Setting proxy chain at: " + proxyIP + ":" + proxyport);
				applicationConfiguration.setProxy(InetAddress.getByName(proxyIP), proxyport);
			}
		} catch (UnknownHostException e) {
			//TODO: SOMEBODY HELP
			e.printStackTrace();
			return;
		}

		// This must be last, as it requires a configuration.
		httpListener = new HttpProxyClientListener(port);
	}

	public ApplicationConfiguration getApplicationConfiguration() {
		return applicationConfiguration;
	}

	public Logger getLogger() {
		return logger;
	}

	public ServerManager getServerManager() {
		return serverManager;
	}

	/**
	 * The main method of the Application class. Executes the application.
	 */
	public void launch() {
		httpListener.listen();
	}


	public static void main(String[] args) throws UnknownHostException {
		Application application = new Application();
		new ApplicationConfigurationServer(8082);
		application.launch();
	}

	public static Application getInstance() {
		return instance;
	}

}
