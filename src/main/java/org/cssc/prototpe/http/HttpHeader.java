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
		
//		if(field.equals("proxy-connection")) {
//			field = "connection";
//		}
		
		contentMap.put(field, value);
	}
	
	public String getField(String field) {
		return contentMap.get(field);
	}
	
	public void removeField(String field) {
		contentMap.remove(field);
	}
	
	public boolean containsField(String field) {
		return contentMap.containsKey(field);
	}
	
	public Map<String, String> getMap() {
		return contentMap;
	}
}
