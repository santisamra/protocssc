package org.cssc.prototpe.http;

import org.cssc.prototpe.http.exceptions.InvalidMethodStringException;

public enum HttpMethod {

	GET("GET"),
	POST("POST"),
	PUT("PUT");
	
	private String methodString;
	
	private HttpMethod(String methodString) {
		this.methodString = methodString;
	}
	
	public static HttpMethod fromString(String string) {
		if(string == null) {
			throw new IllegalArgumentException("Method string cannot be null.");
		}
		
		for(HttpMethod m: HttpMethod.class.getEnumConstants()) {
			if(m.methodString.equals(string)) {
				return m;
			}
		}
		
		throw new InvalidMethodStringException();
	}
	
	public String toString() {
		return methodString;
	}
}
