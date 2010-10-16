package org.cssc.prototpe.net;

import org.cssc.prototpe.net.interfaces.ClientHandler;

public class HttpProxyClientListener extends ClientListener {
	
	public HttpProxyClientListener(int port) {
		super(port, -1, null);
	}

	@Override
	protected ClientHandler getHandler() {
		return new HttpProxyHandler();
	}

}
