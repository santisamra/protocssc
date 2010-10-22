package org.cssc.prototpe.configuration;



import java.net.InetAddress;
import java.util.Set;
import java.util.TreeSet;

public class Actions {
	private boolean blockAllAccesses;
	private Set<InetAddress> blockedIPs = new TreeSet<InetAddress>();
	private Set<String> blockedURIs = new TreeSet<String>();
	private Set<String> blockedMediaTypes = new TreeSet<String>();
	private double maxContentLength;
	private boolean transforml80;
	private boolean transforml33t;

	public Actions(boolean blockAllAccesses, Set<InetAddress> blockedIPs, Set<String> blockedURIs,
			Set<String> blockedMediaTypes, double maxContentLength, boolean transform180,
			boolean transform133t){
		this.blockAllAccesses=blockAllAccesses;
		this.blockedIPs=blockedIPs;
		this.blockedURIs=blockedURIs;
		this.blockedMediaTypes=blockedMediaTypes;
		this.maxContentLength=maxContentLength;
		this.transforml80=transform180;
		this.transforml33t=transform133t;
	}

	@Override
	public String toString() {
		return "Filter [blockAllAccesses=" + blockAllAccesses + ", blockedIPs="
				+ blockedIPs + ", blockedURIs=" + blockedURIs
				+ ", blockedMediaTypes=" + blockedMediaTypes
				+ ", maxContentLength=" + maxContentLength + ", transforml80="
				+ transforml80 + ", transforml33t=" + transforml33t + "]";
	}
	
	public boolean getBlockAllAccesses() {
		return blockAllAccesses;
	}
	public Set<InetAddress> getBlockedIPs() {
		return blockedIPs;
	}
	public Set<String> getBlockedURIs() {
		return blockedURIs;
	}
	public Set<String> getBlockedMediaTypes() {
		return blockedMediaTypes;
	}
	public double getMaxContentLength() {
		return maxContentLength;
	}
	public boolean getTransforml80() {
		return transforml80;
	}
	public boolean getTransforml33t() {
		return transforml33t;
	}
	
}
