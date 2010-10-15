package org.cssc.prototpe.configuration;


import java.io.IOException;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ConfigurationManager {
	private Boolean blockAllAccesses;
	private Set<IP> blockedIPs = new TreeSet<IP>();
	private Set<String> blockedURIs = new TreeSet<String>();
	private Set<String> blockedMediaTypes = new TreeSet<String>();
	private Double maxContentLength;
	private Boolean transforml80;
	private Boolean transforml33t;
	
	private static ConfigurationManager instance;
	
	private ConfigurationManager(){
		try {
			parse();
		} catch (Exception e) {
			//mal formado el XML que hacer ??
		}
	}
	
	public static ConfigurationManager getInstance(){
		if(instance==null){
			instance = new ConfigurationManager();
		}
		return instance;
	}
	
	public String toString(){
		return blockAllAccesses+"\nIP"+blockedIPs.toString()+"\nsize"+blockedIPs.size()+"\nURIS"+blockedURIs.toString()+"\nsize"+blockedURIs.size()
		+"\nMT"+blockedMediaTypes.toString()+"\nsize"+blockedMediaTypes.size()+"\nCL"+maxContentLength+"\n144"+transforml33t+"\n180"+transforml80;
	}
	private void parse() throws SAXException, IOException{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    
	    try {
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse( System.getProperty("user.dir")+"\\config.xml");

	    	NodeList collection = document.getChildNodes().item(0).getChildNodes();
	    	
	    	//for validating purposes only
	    	factory.setValidating(true);
	    	//for namespace awareness
	    	factory.setNamespaceAware(true);
	    	for(int i=0; i<100; i++){ //por cada config
	    		Node currNode = collection.item(i);
	    		if(currNode.getNodeName().equals("block-all-accesses")){
	    			blockAllAccesses=currNode.getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
	    		}
	    		else if(currNode.getNodeName().equals("blocked-IPs")){
	    			 NodeList ips = currNode.getChildNodes();
	    			 for(int j=0; j<ips.getLength(); j++){
	    				 if(ips.item(j).getNodeName().equals("IP")){
	    					 try {
								IP ip = new IP(ips.item(j).getChildNodes().item(0).getTextContent());
								blockedIPs.add(ip);
	    					 } catch (Exception e) {
								break; //lo salteo si el ip es invalido
	    					 }
	    				 }
	    			 }
	    		}
	    		else if(currNode.getNodeName().equals("blocked-URIs")){
	    			 NodeList uris = currNode.getChildNodes();
	    			 for(int j=0; j<uris.getLength(); j++){
	    				 if(uris.item(j).getNodeName().equals("URI")){
    						 blockedURIs.add(uris.item(j).getChildNodes().item(0).getTextContent());
	    				 }
	    			 }
	    		}
	    		else if(currNode.getNodeName().equals("blocked-MediaTypes")){
	    			 NodeList mediaTypes = currNode.getChildNodes();
	    			 for(int j=0; j<mediaTypes.getLength(); j++){
	    				 if(mediaTypes.item(j).getNodeName().equals("MediaType")){
	    					 blockedMediaTypes.add(mediaTypes.item(j).getChildNodes().item(0).getTextContent());
	    				 }
	    			 }
	    		}
	    		else if(currNode.getNodeName().equals("max-content-length")){
	    			try{
	    				maxContentLength=Double.parseDouble(currNode.getChildNodes().item(0).getTextContent());
	    			}catch (NumberFormatException e) {
	    				//si no es un numero (double), lo salteo y lo pongo en null
	    				maxContentLength=null;
					}
	    		}
	    		else if(currNode.getNodeName().equals("transform")){
	    			 NodeList transforms = currNode.getChildNodes();
	    			 for(int j=0; j<transforms.getLength(); j++){
	    				 if(transforms.item(j).getNodeName().equals("l33t")){
	    					 transforml33t=transforms.item(j).getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
	    				 }else if(transforms.item(j).getNodeName().equals("images180")){
	    					 transforml80=transforms.item(j).getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
	    				 }
	    			 }
	    		}
	    	}
	    } catch (SAXParseException spe) {
	    	throw new SAXException(":"+spe.getLineNumber()+":"+spe.getColumnNumber());
	    } catch (SAXException se){
	    	throw new SAXException(":parseError");
	    } catch (ParserConfigurationException pce) {
	    	// Parser with specified options can't be built
	    	throw new SAXException(":unknownError");
	    }catch (IOException ioe){
	    	throw new IOException();
	    }
	}
}
