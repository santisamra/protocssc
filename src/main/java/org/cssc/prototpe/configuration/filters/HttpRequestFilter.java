package org.cssc.prototpe.configuration.filters;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.List;

import org.cssc.prototpe.configuration.filters.application.ApplicationFilter;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.exceptions.MissingHostException;
import org.cssc.prototpe.net.Application;

public class HttpRequestFilter extends Filter {

	private HttpRequest request;

	public HttpRequestFilter(Socket clientSocket, HttpRequest request) {
		super(clientSocket);
		this.request = request;
	}

	public boolean filter() throws IOException {
		ApplicationFilter filter = Application.getInstance().getApplicationConfiguration().getFilterForCondition(clientSocket.getInetAddress(), request.getHeader().getField("user-agent"));

		System.out.println("Filter: " + filter);
		
		if(filter != null) {
			return applyFilter(filter);
		}

		return false;
	}

	private boolean applyFilter(ApplicationFilter filter) throws IOException {
		boolean allAccessesBlocked = filter.isAllAccessesBlocked();
		List<InetAddress> blockedIPs = filter.getBlockedIPs();
		List<String> blockedURIs = filter.getBlockedURIs();
		
		try {
			if(allAccessesBlocked) {
				writeResponse("src/main/resources/html/errors/accessDenied.html");
				clientSocket.close();
				return true;
			} else if(blockedIPs != null && blockedIPs.contains(InetAddress.getByName(request.getEffectiveHost()))) {
				writeResponse("src/main/resources/html/errors/ipAccessDenied.html");
				clientSocket.close();
				return true;
			} else if(blockedURIs != null) {
				String requestUri = "http://" + request.getEffectiveHost() + request.getEffectivePath();
				
				for(String s: filter.getBlockedURIs()) {
					if(uriMatchesExpression(requestUri, s)) {
						writeResponse("src/main/resources/html/errors/uriAccessDenied.html");
						clientSocket.close();
						return true;
					}
				}
			}
		} catch(MissingHostException e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
	private boolean uriMatchesExpression(String uri, String expression) {
		if(expression.startsWith("http://")) {
			expression = expression.substring(7);
		}
		
		if(!expression.startsWith("www.")) {
			expression = "www." + expression;
		}
		
		expression = "http://" + expression;
		
		String regExp = expression;
		if(uri.charAt(uri.length() - 1) == '/' && regExp.charAt(regExp.length() - 1) != '/') {
			int index = expression.lastIndexOf('*');
			
			if(index != -1) {
				char character = expression.charAt(index - 1);
				
				if(character != '/') {
					regExp = regExp + "/";
				}
			} else {
				regExp = regExp + "/";
			}
		}
		
		System.out.println("URI: " + uri);
		System.out.println("Expresion regular: " + regExp);
		regExp = regExp.replace("*", ".*");
		
		return uri.matches(regExp);
	}


}
