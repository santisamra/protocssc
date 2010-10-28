package org.cssc.prototpe.configuration;

import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.cssc.prototpe.configuration.exceptions.ConfigurationParserException;
import org.cssc.prototpe.configuration.filters.application.ApplicationFilter;
import org.cssc.prototpe.configuration.filters.application.FilterCondition;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class ConfigurationParser {
	
	public List<ApplicationFilter> parse(String xmlPath) {
		List<ApplicationFilter> ret = new LinkedList<ApplicationFilter>();
		
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		FilterCondition condition = null;
		ApplicationFilter filter = null;
		List<InetAddress> originIPs = null;
		String browser = null;
		String oS = null;
		boolean blockAllAccesses=false;
		List<InetAddress> blockedIPs = null;
		List<String> blockedURIs = null;
		List<String> blockedMediaTypes = null;
		int maxContentLength = -1;
		boolean l33tTransform = false;
		boolean rotateImages = false;
		
	    try {
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse(xmlPath);
	    	//for validating purposes only
	    	factory.setValidating(true);
	    	//for namespace awareness
	    	factory.setNamespaceAware(true);
	    	
	    	for(int p = 0; p < document.getChildNodes().getLength(); p++){
	    		if(document.getChildNodes().item(p).getNodeName().equals("config")){
	    			NodeList configs = document.getChildNodes().item(p).getChildNodes();
	
			    	for(int l = 0; l < configs.getLength(); l++) {
			    		if(configs.item(l).getNodeName().equals("filters")) {
			    			NodeList filtersNodeList = configs.item(l).getChildNodes();
			    			for(int n = 0; n < filtersNodeList.getLength(); n++){
			    	    		if(filtersNodeList.item(n).getNodeName().equals("filter")){
				    				NodeList filterConf = filtersNodeList.item(n).getChildNodes();
				    				for(int conf = 0; conf < filterConf.getLength(); conf++) {
				    					if(filterConf.item(conf).getNodeName().equals("conditions")){
				    						NodeList conditions = filterConf.item(conf).getChildNodes();
				    				    	for(int i = 0; i < conditions.getLength(); i++) { //por cada condition
				    				    		Node currNode = conditions.item(i);
				    				    		if(currNode.getNodeName().equals("origin-IPs")){
				    				    			originIPs = new LinkedList<InetAddress>();
				    				    			 NodeList ips = currNode.getChildNodes();
				    				    			 for(int j = 0; j < ips.getLength(); j++) {
				    				    				 if(ips.item(j).getNodeName().equals("IP")){
				    				    					 try {
				    											IP ip = new IP(ips.item(j).getChildNodes().item(0).getTextContent());
				    											originIPs.add(ip.getInetAddress());
				    				    					 } catch (Exception e) {
				    											break; //lo salteo si el ip es invalido
				    				    					 }
				    				    				 }
				    				    			 }
				    				    		}
				    				    		else if(currNode.getNodeName().equals("browser")){
				    				    			browser=currNode.getChildNodes().item(0).getTextContent();
				    				    		}
				    				    		else if(currNode.getNodeName().equals("OS")){
				    				    			oS=currNode.getChildNodes().item(0).getTextContent();
				    				    		}
				    				    	}
				    					}
				    					else if(filterConf.item(conf).getNodeName().equals("actions")){
				    						NodeList actionsNodeList = filterConf.item(conf).getChildNodes();
				    				    	for(int i=0; i<actionsNodeList.getLength(); i++){ //por cada config
				    				    		Node currNode = actionsNodeList.item(i);
				    				    		if(currNode.getNodeName().equals("block-all-accesses")){
				    				    			blockAllAccesses=currNode.getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
				    				    		}
				    				    		else if(currNode.getNodeName().equals("blocked-IPs")){
				    				    			blockedIPs = new LinkedList<InetAddress>();
				    				    			 NodeList ips = currNode.getChildNodes();
				    				    			 for(int j=0; j<ips.getLength(); j++){
				    				    				 if(ips.item(j).getNodeName().equals("IP")){
				    				    					 try {
				    											IP ip = new IP(ips.item(j).getChildNodes().item(0).getTextContent());
				    											blockedIPs.add(ip.getInetAddress());
				    				    					 } catch (Exception e) {
				    											break; //lo salteo si el ip es invalido
				    				    					 }
				    				    				 }
				    				    			 }
				    				    		}
				    				    		else if(currNode.getNodeName().equals("blocked-URIs")){
				    				    			blockedURIs = new LinkedList<String>();
				    				    			 NodeList uris = currNode.getChildNodes();
				    				    			 for(int j=0; j<uris.getLength(); j++){
				    				    				 if(uris.item(j).getNodeName().equals("URI")){
				    			    						 blockedURIs.add(uris.item(j).getChildNodes().item(0).getTextContent());
				    				    				 }
				    				    			 }
				    				    		}
				    				    		else if(currNode.getNodeName().equals("blocked-MediaTypes")){
				    				    			blockedMediaTypes = new LinkedList<String>();
				    				    			 NodeList mediaTypes = currNode.getChildNodes();
				    				    			 for(int j=0; j<mediaTypes.getLength(); j++){
				    				    				 if(mediaTypes.item(j).getNodeName().equals("MediaType")){
				    				    					 blockedMediaTypes.add(mediaTypes.item(j).getChildNodes().item(0).getTextContent());
				    				    				 }
				    				    			 }
				    				    		}
				    				    		else if(currNode.getNodeName().equals("max-content-length")){
				    				    			try {
				    				    				maxContentLength = Integer.parseInt(currNode.getChildNodes().item(0).getTextContent());
				    				    				
				    				    				if(maxContentLength < 0) {
				    				    					throw new ConfigurationParserException("Max content length cannot be less than 0.");
				    				    				}
				    				    			}catch (NumberFormatException e) {
				    				    				throw new ConfigurationParserException("Invalid max content length.");
				    								}
				    				    		}
				    				    		else if(currNode.getNodeName().equals("transform")) {
				    				    			 NodeList transforms = currNode.getChildNodes();
				    				    			 
				    				    			 for(int j = 0; j < transforms.getLength(); j++) {
				    				    				 if(transforms.item(j).getNodeName().equals("l33t")) {
				    				    					 l33tTransform=transforms.item(j).getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
				    				    				 } else if(transforms.item(j).getNodeName().equals("images180")) {
				    				    					 rotateImages=transforms.item(j).getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
				    				    				 }
				    				    			 }
				    				    		}
				    				    	}
//				    				    	actions = new Actions(blockAllAccesses, blockedIPs, blockedURIs, 
//				    				    			blockedMediaTypes, maxContentLength, rotateImages, l33tTransform);
				    					}
				    				}
//				    				filter = new Filter(actions, originIPs, browser, oS);
//				    				filters.add(filter);
				    				
				    				condition = new FilterCondition(originIPs, browser, oS);
				    				
				    				filter = new ApplicationFilter(
											condition,
											blockAllAccesses,
											blockedIPs,
											blockedURIs,
											blockedMediaTypes,
											maxContentLength,
											l33tTransform,
											rotateImages
									);
				    				
				    				ret.add(filter);
			    	    		}
			    			}
			    		}
			    	}
		    	}
	    	}
	    	
	    	return ret;
	    	
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new ConfigurationParserException("Could not parse the configuration file.");
	    }
	}
}
