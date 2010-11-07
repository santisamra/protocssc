package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.httpserver.model.HttpServletRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;

public class MonitorServlet extends MyHttpServlet{

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if( !validateAuth() ){
			return;
		}
		response.getBuffer().append("Monitor");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		doGet(request, response);
	}

}
