package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;

public class IndexServlet extends MyHttpServlet {

	public IndexServlet(String mapping) {
		super(mapping);
	}

	@Override
	public void doGet(HttpRequest request, HttpServletResponse response)
			throws IOException {
		response.sendRedirect("http://www.google.com");
	}

	@Override
	public void doPost(HttpRequest request, HttpServletResponse response)
			throws IOException {

	}

}
