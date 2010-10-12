package org.cssc.prototpe.http;

import java.util.HashMap;
import java.util.Map;

public class HttpHeader {

	private Map<String, String> contentMap;
	
	public HttpHeader() {
		this.contentMap = new HashMap<String, String>();
	}
	
	public void setField(String field, String value) {
		if(field == null) {
			throw new IllegalArgumentException("Field cannot be null.");
		}
		
		if(value == null) {
			throw new IllegalArgumentException("Value cannot be null.");
		}
		
		contentMap.put(field, value);
	}
	
	public String getField(String field) {
		return contentMap.get(field);
	}
	
	//TODO: Funcion de testing.
	public Map<String, String> getContentMap() {
		return contentMap;
	}
}
