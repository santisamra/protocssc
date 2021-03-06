package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.configuration.ApplicationConfiguration;
import org.cssc.prototpe.httpserver.model.HttpServletRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;
import org.cssc.prototpe.net.Application;

public class ConfigurationServlet extends MyHttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.getBuffer()
		.append("<html><body>You may only POST to this page.</body></html>");
	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		if( !validateAuth()){
			return;
		}
		
		String content = request.getContent();
		
		StringBuffer buf = response.getBuffer();
		buf.append("<html><body>");
		
		buf.append("<h2>Configuration File uploaded successfuly</h2>");
		
		buf.append("<h4>This is the file content:</h4>");
		
		buf.append("<textarea rows=\"20\" cols=\"60\">" + content + "</textarea>");
		
		buf.append("<br/><a href=\"/\">Back</a>");
		
		buf.append("</body></html>");
		
		if( content != null ){
			Application.getInstance().setApplicationConfigurationRuntime(new ApplicationConfiguration(content));
		}
	}

}
