package org.cssc.prototpe.configuration.filters.application;

import java.util.List;

public class ApplicationFilter {
	
	private FilterCondition condition;

	/* Actions to apply. */
	private boolean allAccessesBlocked;
	private List<String> blockedIPs;
	private List<String> blockedURIs;
	private List<String> blockedMediaTypes;
	private int maxContentLength;
	private boolean l33tTransform;
	private boolean rotateImages;
	
	public ApplicationFilter(FilterCondition condition, boolean allAccessesAreBlocked,
			List<String> blockedIPs, List<String> blockedURIs,
			List<String> blockedMediaTypes, int maxContentLength,
			boolean l33tTransform, boolean rotateImages) {
		super();
		this.condition = condition;
		this.allAccessesBlocked = allAccessesAreBlocked;
		this.blockedIPs = blockedIPs;
		this.blockedURIs = blockedURIs;
		this.blockedMediaTypes = blockedMediaTypes;
		this.maxContentLength = maxContentLength;
		this.l33tTransform = l33tTransform;
		this.rotateImages = rotateImages;
	}

	public void setCondition(FilterCondition condition) {
		this.condition = condition;
	}
	
	public FilterCondition getCondition() {
		return condition;
	}
	
	public boolean isAllAccessesBlocked() {
		return allAccessesBlocked;
	}
	
	public void setAllAccessesBlocked(boolean allAccessesBlocked) {
		this.allAccessesBlocked = allAccessesBlocked;
	}
	
	public List<String> getBlockedIPs() {
		return blockedIPs;
	}
	
	public void setBlockedIPs(List<String> blockedIPs) {
		this.blockedIPs = blockedIPs;
	}
	
	public List<String> getBlockedURIs() {
		return blockedURIs;
	}
	
	public void setBlockedURIs(List<String> blockedURIs) {
		this.blockedURIs = blockedURIs;
	}
	
	public List<String> getBlockedMediaTypes() {
		return blockedMediaTypes;
	}
	
	public void setBlockedMediaTypes(List<String> blockedMediaTypes) {
		this.blockedMediaTypes = blockedMediaTypes;
	}
	
	public int getMaxContentLength() {
		return maxContentLength;
	}
	
	public void setMaxContentLength(int maxContentLength) {
		this.maxContentLength = maxContentLength;
	}
	
	public boolean isL33tTransform() {
		return l33tTransform;
	}
	
	public void setL33tTransform(boolean l33tTransform) {
		this.l33tTransform = l33tTransform;
	}
	
	public boolean isRotateImages() {
		return rotateImages;
	}
	
	public void setRotateImages(boolean rotateImages) {
		this.rotateImages = rotateImages;
	}
}
