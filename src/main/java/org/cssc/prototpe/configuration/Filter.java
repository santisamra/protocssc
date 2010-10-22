package org.cssc.prototpe.configuration;



import java.net.InetAddress;
import java.util.Set;

public class Filter implements Comparable<Filter>{
	private Actions actions;
	private Set<InetAddress> originIPs;
	private String browser;
	private String oS;

	public Filter(Actions actions, Set<InetAddress> originIPs, String browser, String oS){
		this.actions=actions;
		this.originIPs=originIPs;
		this.browser=browser;
		this.oS=oS;
	}
	@Override
	public String toString() {
		return "User [filter=" + actions + ", originIPs=" + originIPs
				+ ", browser=" + browser + ", oS=" + oS + "]";
	}
	public Actions getFilter() {
		return actions;
	}
	public Set<InetAddress> getOriginIPs() {
		return originIPs;
	}
	public String getBrowser() {
		return browser;
	}
	public String getoS() {
		return oS;
	}	
	@Override
	public int compareTo(Filter u) {
		return u.getOriginIPs().equals(this.originIPs)?0:1;
	}
}
