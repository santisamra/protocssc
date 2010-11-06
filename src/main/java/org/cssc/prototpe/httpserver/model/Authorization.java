package org.cssc.prototpe.httpserver.model;

public class Authorization {

	private String username;
	private String password;
	
	public Authorization(String username, String password){
		this.username = username;
		this.password = password;
	}
	
	public String getUsername(){
		return username;
	}
	
	public String getPassword(){
		return password;
	}
	
	@Override
	public String toString(){
		return "Username: " + username + " Password: " + password; 
	}
}
