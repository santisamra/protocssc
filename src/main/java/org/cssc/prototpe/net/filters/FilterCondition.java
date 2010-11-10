package org.cssc.prototpe.net.filters;

import java.net.InetAddress;
import java.util.List;

public class FilterCondition {

	private List<InetAddress> ips;
	private String browser;
	private String operatingSystem;
	
	public FilterCondition(List<InetAddress> ips, String browser,
			String operatingSystem) {
		this.ips = ips;
		this.browser = browser;
		this.operatingSystem = operatingSystem;
	}

	public List<InetAddress> getIps() {
		return ips;
	}
	
	public void setIps(List<InetAddress> ips) {
		this.ips = ips;
	}

	public String getBrowser() {
		return browser;
	}

	public void setBrowser(String browser) {
		this.browser = browser;
	}

	public String getOperatingSystem() {
		return operatingSystem;
	}

	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ips == null) ? 0 : ips.hashCode());
		result = prime * result + ((browser == null) ? 0 : browser.hashCode());
		result = prime * result
				+ ((operatingSystem == null) ? 0 : operatingSystem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FilterCondition other = (FilterCondition) obj;
		if (ips == null) {
			if (other.ips != null)
				return false;
		} else if (!ips.equals(other.ips))
			return false;
		if (browser == null) {
			if (other.browser != null)
				return false;
		} else if (!browser.equals(other.browser))
			return false;
		if (operatingSystem == null) {
			if (other.operatingSystem != null)
				return false;
		} else if (!operatingSystem.equals(other.operatingSystem))
			return false;
		return true;
	}
	
	
}
