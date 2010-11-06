package org.cssc.prototpe.httpserver.servlets;

import java.io.IOException;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.httpserver.model.HttpServletResponse;
import org.cssc.prototpe.httpserver.model.MyHttpServlet;
import org.cssc.prototpe.net.Application;
import org.cssc.prototpe.net.ApplicationConfiguration;

public class IndexServlet extends MyHttpServlet {

	@Override
	public void doGet(HttpRequest request, HttpServletResponse response)
			throws IOException {
		//Must implement a way of getting url GET parameters
		ApplicationConfiguration configuration = Application.getInstance().getApplicationConfiguration();
		
		String authToken = null;
		if( authToken == null ){
			response.unauthorize();
			System.out.println(request.getHeader().getField("authorization"));
			return;
		}
		
		StringBuffer buf = response.getBuffer();
		buf.append("<html><body>");
		
		buf.append("<h1>CSSC Proxy Server Configuration</h1>");
		
		buf.append("<table>");
		buf.append("<tr>");
		buf.append("<td>Current Port: </td>");
		buf.append("<td>"+configuration.getProxyPort()+"</td>");
		buf.append("</tr>");
		
		buf.append("<tr>");
		buf.append("<td>Current Port: </td>");
		buf.append("<td>"+configuration.getProxyPort()+"</td>");
		buf.append("</tr>");
		
		buf.append("</table>");
		
		buf.append("</body></html>");
		
	}

	@Override
	public void doPost(HttpRequest request, HttpServletResponse response)
			throws IOException {

	}

}
