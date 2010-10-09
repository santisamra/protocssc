package org.cssc.prototpe.http;

import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;

public enum HttpResponseCode {

	OK(200),
	NOT_FOUND(404);
	
	private int code;
	
	private HttpResponseCode(int code) {
		this.code = code;
	}
	
	public static HttpResponseCode fromString(int code) {
		for(HttpResponseCode r: HttpResponseCode.class.getEnumConstants()) {
			if(r.code == code) {
				return r;
			}
		}
		
		throw new InvalidStatusCodeException();
	}
}
