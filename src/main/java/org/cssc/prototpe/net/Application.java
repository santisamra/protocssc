package org.cssc.prototpe.net;

import java.io.File;
import java.net.UnknownHostException;
import java.util.List;

import org.cssc.prototpe.httpserver.ApplicationConfigurationServer;
import org.cssc.prototpe.net.clients.ClientListener;
import org.cssc.prototpe.net.clients.HttpProxyClientListener;
import org.cssc.prototpe.net.filters.ApplicationFilter;
import org.cssc.prototpe.net.interfaces.ServerManager;
import org.cssc.prototpe.net.monitoring.MonitoringService;
import org.cssc.prototpe.net.servers.PersistentSemaphorizedServerManager;


/**
 * The main class for the application. 
 */
public class Application {

	private static Application instance = null;
	private ApplicationConfiguration applicationConfiguration;
	private ClientListener httpListener;
	private Logger logger;
	private ServerManager serverManager;
	private MonitoringService monitoringService;

	private static final String CONFIG_FILE = "config.xml";

	private Application() {
		// This must be first, before anything else, as other parts of the
		// application require this instance.
		instance = this;

		// This MUST be second as other parts of the application require this configuration.
		applicationConfiguration = new ApplicationConfiguration(new File(CONFIG_FILE));

		logger = new Logger(applicationConfiguration.getLoggingFileName());
//		serverManager = new SimpleServerManager();
//		serverManager = new PersistentServerManager(30);
		serverManager = new PersistentSemaphorizedServerManager(
				applicationConfiguration.getMaxPersistantServerConnections(),
				applicationConfiguration.getMaxPersistantServerConnectionsPerServer());
		monitoringService = new MonitoringService();

		// This must be last, as it requires a configuration.
		httpListener = new HttpProxyClientListener(getApplicationConfiguration().getProxyPort());
	}
	
	public void setApplicationConfiguration(
			ApplicationConfiguration applicationConfiguration) {
		this.applicationConfiguration = applicationConfiguration;
	}
	
	public void setApplicationConfigurationRuntime(
			ApplicationConfiguration applicationConfiguration) {
		List<ApplicationFilter> newFilters = applicationConfiguration.getFilters();
		if( newFilters != null && !newFilters.isEmpty()){
			this.applicationConfiguration.replaceAllFilters(newFilters);
		}
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
	
	public MonitoringService getMonitoringService() {
		return monitoringService;
	}

	/**
	 * The main method of the Application class. Executes the application.
	 */
	public void launch() {
		httpListener.listen();
	}


	public static void main(String[] args) throws UnknownHostException {
		Application application = new Application();
		new ApplicationConfigurationServer(Application.getInstance().getApplicationConfiguration().getRemoteServicesPort());
		System.out.println("HTTP Proxy Server started at port " + Application.getInstance().getApplicationConfiguration().getProxyPort() + ".");
		System.out.println("Remote services started at port " + Application.getInstance().getApplicationConfiguration().getRemoteServicesPort() + ".");
		application.launch();
	}

	public static Application getInstance() {
		return instance;
	}

}
