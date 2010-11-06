package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;

public class LoginServlet extends MyHttpServlet {

	@Override
	public void doGet(HttpRequest request, HttpServletResponse response)
			throws IOException {
		response.forward("src/main/resources/html/manager/login.html");
	}

	@Override
	public void doPost(HttpRequest request, HttpServletResponse response)
			throws IOException {

	}

}
