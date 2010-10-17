package org.cssc.prototpe.http;

import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;

public enum HttpResponseCode {

	OK(200),
	NO_CONTENT(204),
	PARTIAL_CONTENT(206),
	FOUND(302),
	NOT_FOUND(404);
	
	private int code;
	
	private HttpResponseCode(int code) {
		this.code = code;
	}
	
	public static HttpResponseCode fromInt(int code) {
		for(HttpResponseCode r: HttpResponseCode.class.getEnumConstants()) {
			if(r.code == code) {
				return r;
			}
		}
		
		System.out.println("Invalid code: " + code);
		throw new InvalidStatusCodeException();
	}
	
	public int getCode() {
		return code;
	}
}
