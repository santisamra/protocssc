package org.cssc.prototpe.configuration;



public class IP implements Comparable<IP>{
	private String ip;
	
	public IP(String ip) throws Exception{
		validate(ip);
		this.ip=ip;
	}
	
	private void validate(String ip) throws Exception{
		String ips[]=ip.split("\\.");
		if(ips.length!=4)
			throw new Exception();
		for(String nr: ips){
			try{
				Integer.parseInt(nr);
			}catch(NumberFormatException e){
				throw new Exception();
			}
		}
	}
	public String getIP(){
		return this.ip;
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
