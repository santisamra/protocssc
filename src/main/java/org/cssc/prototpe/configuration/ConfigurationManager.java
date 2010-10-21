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
	
	private static ConfigurationManager instance;
	private Set<User> users;
	
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
	private void parse() throws SAXException, IOException{
	    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Filter filter=null;
		User user=null;
		Set<IP> originIPs=null;
		String browser=null;
		String oS=null;
		boolean blockAllAccesses=false;
		Set<IP> blockedIPs = null;
		Set<String> blockedURIs = null;
		Set<String> blockedMediaTypes = null;
		double maxContentLength=0;
		boolean transforml80=false;
		boolean transforml33t=false;
	    try {
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    	Document document = builder.parse( System.getProperty("user.dir")+"\\config.xml");
	    	//for validating purposes only
	    	factory.setValidating(true);
	    	//for namespace awareness
	    	factory.setNamespaceAware(true);

	    	for(int p=0; p<document.getChildNodes().getLength(); p++){
	    		if(document.getChildNodes().item(p).getNodeName().equals("config")){
	    			NodeList configs = document.getChildNodes().item(p).getChildNodes();
	
			    	for(int l=0; l<configs.getLength(); l++){
			    		if(configs.item(l).getNodeName().equals("filters")){
			    			users = new TreeSet<User>();
			    			NodeList filters = configs.item(l).getChildNodes();
			    			for(int n=0; n<filters.getLength(); n++){
			    	    		if(filters.item(n).getNodeName().equals("filter")){
				    				NodeList filterConf = filters.item(n).getChildNodes();
				    				for(int conf=0; conf<filterConf.getLength(); conf++){
				    					if(filterConf.item(conf).getNodeName().equals("conditions")){
				    						NodeList conditions = filterConf.item(conf).getChildNodes();
				    				    	for(int i=0; i<conditions.getLength(); i++){ //por cada condition
				    				    		Node currNode = conditions.item(i);
				    				    		if(currNode.getNodeName().equals("origin-IPs")){
				    				    			originIPs = new TreeSet<IP>();
				    				    			 NodeList ips = currNode.getChildNodes();
				    				    			 for(int j=0; j<ips.getLength(); j++){
				    				    				 if(ips.item(j).getNodeName().equals("IP")){
				    				    					 try {
				    											IP ip = new IP(ips.item(j).getChildNodes().item(0).getTextContent());
				    											originIPs.add(ip);
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
				    						NodeList actions = filterConf.item(conf).getChildNodes();
				    				    	for(int i=0; i<actions.getLength(); i++){ //por cada config
				    				    		Node currNode = actions.item(i);
				    				    		if(currNode.getNodeName().equals("block-all-accesses")){
				    				    			blockAllAccesses=currNode.getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
				    				    		}
				    				    		else if(currNode.getNodeName().equals("blocked-IPs")){
				    				    			blockedIPs = new TreeSet<IP>();
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
				    				    			blockedURIs = new TreeSet<String>();
				    				    			 NodeList uris = currNode.getChildNodes();
				    				    			 for(int j=0; j<uris.getLength(); j++){
				    				    				 if(uris.item(j).getNodeName().equals("URI")){
				    			    						 blockedURIs.add(uris.item(j).getChildNodes().item(0).getTextContent());
				    				    				 }
				    				    			 }
				    				    		}
				    				    		else if(currNode.getNodeName().equals("blocked-MediaTypes")){
				    				    			blockedMediaTypes = new TreeSet<String>();
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
				    				    				//si no es un numero (double), lo salteo y lo pongo en 0 (default)
				    				    				maxContentLength=0;
				    								}
				    				    		}
				    				    		else if(currNode.getNodeName().equals("transform")){
				    				    			 NodeList transforms = currNode.getChildNodes();
//				    				    			 System.out.println("transf");
//				    				    			 System.out.println(transforms);
				    				    			 for(int j=0; j<transforms.getLength(); j++){
//				    				    				 System.out.println(j);
//				    				    				 System.out.println(transforms.item(j).getNodeName());
				    				    				 if(transforms.item(j).getNodeName().equals("tl33t")){
//				    				    					 System.out.println(transforms.item(j).getChildNodes().item(0));
				    				    					 transforml33t=transforms.item(j).getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
//				    				    					 System.out.println("lala1");
				    				    				 }else if(transforms.item(j).getNodeName().equals("images180")){
//				    				    					 System.out.println("lala2");
				    				    					 transforml80=transforms.item(j).getChildNodes().item(0).getTextContent().equalsIgnoreCase("true")?true:false;
//				    				    					 System.out.println("lala3");
				    				    				 }
				    				    			 }
				    				    		}
//				    				    		System.out.println(currNode.getNodeName());
				    				    	}
//				    				    	System.out.println("antess");
				    				    	filter = new Filter(blockAllAccesses, blockedIPs, blockedURIs, 
				    				    			blockedMediaTypes, maxContentLength, transforml80, transforml33t);
//				    				    	System.out.println(filter);
				    					}
				    				}
//				    				System.out.println("antes1");
				    				user = new User(filter, originIPs, browser, oS);
				    				System.out.println(user);
				    				users.add(user);
//				    				System.out.println("despues");
			    	    		}
			    			}
			    		}
			    	}
		    	}
	    	}
	    	System.out.println(users.size());
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

	@Override
	public String toString() {
		return "ConfigurationManager [users=" + users + "]";
	}
}
