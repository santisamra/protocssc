package org.cssc.prototpe.configuration;


import java.util.Set;

public class User implements Comparable<User>{
	private Filter filter;
	private Set<IP> originIPs;
	private String browser;
	private String oS;

	public User(Filter filter, Set<IP> originIPs, String browser, String oS){
		this.filter=filter;
		this.originIPs=originIPs;
		this.browser=browser;
		this.oS=oS;
	}
	@Override
	public String toString() {
		return "User [filter=" + filter + ", originIPs=" + originIPs
				+ ", browser=" + browser + ", oS=" + oS + "]";
	}
	public Filter getFilter() {
		return filter;
	}
	public Set<IP> getOriginIPs() {
		return originIPs;
	}
	public String getBrowser() {
		return browser;
	}
	public String getoS() {
		return oS;
	}	
	@Override
	public int compareTo(User u) {
		return u.getOriginIPs().equals(this.originIPs)?0:1;
	}
}
