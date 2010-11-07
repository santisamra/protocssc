package org.cssc.prototpe.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

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

	private Application() {
		// This must be first, before anything else, as other parts of the
		// application require this instance.
		instance = this;

		// This MUST be second as other parts of the application require this configuration.
		applicationConfiguration = new ApplicationConfiguration();
		applicationConfiguration.loadInitialValues(CONFIG_FILE);
		//TODO: this is a parche

		// TODO: Place here any parts of the application that are needed by other parts.
		logger = new Logger(applicationConfiguration.getLoggingFileName());
//		serverManager = new SimpleServerManager();
		serverManager = new PersistentSemaphorizedServerManager(applicationConfiguration.getMaxPersistantServerConnections(),
				applicationConfiguration.getMaxPersistantServerConnectionsPerServer());

		//TODO: Nada validado obviamente.. TESTING PURO
		int port = 8080; 

//		try{
//			port = Integer.valueOf(JOptionPane.showInputDialog("Port?"));
//		}catch(NumberFormatException e){
//			port = 8080;
//		}
//		if( port <= 0 || port >= 65536){
//			port = 8080;
//		}
//
//		System.out.println("Starting server at port " + port);
//		String proxyIP = JOptionPane.showInputDialog("Proxy Chaining IP? Otherwise leave empty");
//		int proxyport;
//		try{
//			proxyport = Integer.valueOf(JOptionPane.showInputDialog("Proxy Chaining Port? Otherwise leave empty"));
//		} catch (NumberFormatException e){
//			proxyport = 0;
//		}
//		try {
//			if( proxyIP != null && proxyport != 0){
//				System.out.println("Setting proxy chain at: " + proxyIP + ":" + proxyport);
//				applicationConfiguration.setChainingProxy(InetAddress.getByName(proxyIP), proxyport);
//			}
//		} catch (UnknownHostException e) {
//			//TODO: SOMEBODY HELP
//			e.printStackTrace();
//			return;
//		}

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
		System.out.println("HTTP Proxy Server started.");
		application.launch();
	}

	public static Application getInstance() {
		return instance;
	}

}
