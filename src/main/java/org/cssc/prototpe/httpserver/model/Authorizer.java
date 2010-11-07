package org.cssc.prototpe.httpserver.model;

import java.util.Map;

import org.cssc.prototpe.net.Application;

public class Authorizer {

	private static Authorizer instance;
	
	public static Authorizer getInstance(){
		if( instance == null ){
			instance = new Authorizer();
		}
		return instance;
	}
	
	private Authorizer(){}
	
	public boolean authorize(Authorization auth){
		Map<String, String> adminUsers = Application.getInstance()
		.getApplicationConfiguration().getAdminUsers();
		
		String password = adminUsers.get(auth.getUsername());
		if( password == null || !auth.getPassword().equals(password) ){
			return false;
		}
		return true;
	}
}
