package org.cssc.prototpe.configuration;


import java.net.InetAddress;



public class IP implements Comparable<IP>{
	private String ip;
	private InetAddress inetAddress;
	
	public IP(String ip) throws Exception{
		this.inetAddress=validate(ip);
		this.ip=ip;
	}
	
	private InetAddress validate(String ip) throws Exception{
		String ips[]=ip.split("\\.");
		byte iA[]= {0,0,0,0}; 
		int i=0;
		if(ips.length!=4)
			throw new Exception();
		for(String nr: ips){
			try{
				iA[i] = (byte)Integer.parseInt(nr);
				i++;
			}catch(NumberFormatException e){
				throw new Exception();
			}
		}
		return InetAddress.getByAddress(iA);
	}
	public String getIP(){
		return this.ip;
	}

	public InetAddress getInetAddress(){
		return this.inetAddress;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IP){
			return ip.equals(((IP)obj).getIP());
		}
		return false;
	}
	@Override
	public int hashCode() {
		return ip.hashCode();
	}

	@Override
	public int compareTo(IP o) {
		return o.getIP().compareTo(ip);
	}
	
}
