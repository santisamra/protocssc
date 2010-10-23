package org.cssc.prototpe.configuration.filters;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

import org.cssc.prototpe.configuration.filters.application.ApplicationFilter;
import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.http.HttpResponse;
import org.cssc.prototpe.net.Application;

public class HttpResponseFilter extends Filter {

	private HttpRequest request;
	private HttpResponse response;
	
	public HttpResponseFilter(Socket clientSocket, HttpRequest request, HttpResponse response) {
		super(clientSocket);
		this.request = request;
		this.response = response;
	}
	
	public boolean filter() throws IOException {
		ApplicationFilter filter = Application.getInstance().getApplicationConfiguration().getFilterForCondition(clientSocket.getInetAddress(), request.getHeader().getField("user-agent"));

		if(filter != null) {
			return applyFilter(filter);
		}

		return false;
	}

	private boolean applyFilter(ApplicationFilter filter) throws IOException {
		System.out.println("Blocked media types:");
		System.out.println(filter.getBlockedMediaTypes());
		System.out.println("My media type:");
		System.out.println(response.getHeader().getField("content-type"));
		
		List<String> blockedMediaTypes = filter.getBlockedMediaTypes();
		
		if(blockedMediaTypes != null && blockedMediaTypes.contains(response.getHeader().getField("content-type"))) {
			System.out.println("ISOADHDSOIHDSIHDSIHSDA");
			System.out.println("ISOADHDSOIHDSIHDSIHSDA");
			System.out.println("ISOADHDSOIHDSIHDSIHSDA");
			System.out.println("ISOADHDSOIHDSIHDSIHSDA");
			System.out.println("ISOADHDSOIHDSIHDSIHSDA");
			writeResponse("src/main/resources/html/errors/mediaTypeBlocked.html");
			return true;
		}
		
		return false;
	}
}
