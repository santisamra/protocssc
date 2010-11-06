package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;

public class ConfigurationServlet extends MyHttpServlet {

	@Override
	public void doGet(HttpRequest request, HttpServletResponse response)
			throws IOException {
		// TODO Auto-generated method stub

	}

	@Override
	public void doPost(HttpRequest request, HttpServletResponse response)
			throws IOException {
		System.out.println("Hola!");
		response.getBuffer().append("Hello world! Appended file successfuly");
	}

}
