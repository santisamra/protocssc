package org.cssc.prototpe.httpserver.model;

public class MyBase64Decoder {
	
	private static char[] base64encoder = {'A', 'B', 'C', 'D', 'E', 'F','G', 'H', 'I', 
			'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 
			'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 
			'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 
			'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', 
			'9', '+', '/'};
	

	
	public static String encode(byte[] input){
		if( input == null){
			throw new IllegalArgumentException("Can't encode an empty byte array");
		}
		
		StringBuffer result = new StringBuffer();
		
		int i = 0;
		int aux = 0;
		for( i = 0; i < input.length ; i += 3){
			aux = 0;
			aux += input[i];
			aux += (input[i+1]<<8);
			aux += (input[i+2]<<16);
			
			result.append(base64encoder[aux & 0x003F]);
			result.append(base64encoder[aux & 0x0]);
			
		}
		
		return result.toString();
	}

	public static byte[] decode(String input){
		if( input == null){
			throw new IllegalArgumentException("Can't decode the empty string");
		}
		
		int aux = 0;
		
		byte[] decoded = new byte[input.length()];
		
		
		
		
		
		return decoded;
	}
}
