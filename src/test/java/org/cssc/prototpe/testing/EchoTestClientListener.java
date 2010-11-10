package org.cssc.prototpe.testing;

import org.cssc.prototpe.net.clients.ClientListener;
import org.cssc.prototpe.net.interfaces.ClientHandler;

public class EchoTestClientListener extends ClientListener {
	
	public EchoTestClientListener(int port) {
		super(port, -1, null);
	}

	@Override
	protected ClientHandler getHandler() {
		return new EchoHandler(60000, 1024);
	}

}
