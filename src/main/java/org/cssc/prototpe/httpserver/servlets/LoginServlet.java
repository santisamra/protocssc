package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;

public class LoginServlet extends MyHttpServlet {

	public LoginServlet(String mapping) {
		super(mapping);
	}

	@Override
	public void doGet(HttpRequest request, HttpServletResponse response)
			throws IOException {
		//Responds to it's mapping.
	}

	@Override
	public void doPost(HttpRequest request, HttpServletResponse response)
			throws IOException {

	}

}
