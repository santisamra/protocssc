package org.cssc.prototpe.http;

import org.cssc.prototpe.http.exceptions.InvalidStatusCodeException;

public enum HttpResponseCode {

	CONTINUE(100, false),
	SWITCHING_PROTOCOLS(101, false),
	OK(200, true),
	CREATED(201, true),
	ACCEPTED(202, true),
	NON_AUTHORITATIVE_INFORMATION(203, true),
	NO_CONTENT(204, false),
	RESET_CONTENT(205, true),
	PARTIAL_CONTENT(206, true),
	MULTIPLE_CHOICES(300, false),
	MOVED_PERMANENTLY(301, false),
	FOUND(302, false),
	SEE_OTHER(303, false),
	NOT_MODIFIED(304, false),
	USE_PROXY(305, false),
	UNUSED_CODE_306(306, false),
	TEMPORARY_REDIRECT(307, false),
	BAD_REQUEST(400, true),
	UNAUTHORIZED(401, true),
	PAYMENT_REQUIRED(402, true),
	FORBIDDEN(403, true),
	NOT_FOUND(404, true),
	METHOD_NOT_ALLOWED(405, true),
	NOT_ACCEPTABLE(406, true),
	PROXY_AUTHENTICATION_REQUIRED(407, true),
	REQUEST_TIMEOUT(408, true),
	CONFLICT(409, true),
	GONE(410, true),
	LENGTH_REQUIRED(411, true),
	PRECONDITION_FAILED(412, true),
	REQUEST_ENTITY_TOO_LARGE(413, true),
	REQUEST_URI_TOO_LONG(414, true),
	UNSUPPORTED_MEDIA_TYPE(415, true),
	REQUESTED_RANGE_NOT_SATISFIABLE(416, true),
	EXPECTATION_FAILED(417, true),
	INTERNAL_SERVER_ERROR(500, true),
	NOT_IMPLEMENTED(501, true),
	BAD_GATEWAY(502, true),
	SERVICE_UNAVAILABLE(503, true),
	GATEWAY_TIMEOUT(504, true),
	HTTP_VERSION_NOT_SUPPORTED(505, true);
	
	private int code;
	private boolean possibleContent;
	
	public boolean isPossibleContent() {
		return possibleContent;
	}

	private HttpResponseCode(int code, boolean possibleContent) {
		this.code = code;
		this.possibleContent = possibleContent;
	}
	
	public static HttpResponseCode fromInt(int code) throws InvalidStatusCodeException {
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
