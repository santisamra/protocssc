package org.cssc.prototpe.testing;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

import org.cssc.prototpe.http.HttpRequest;
import org.cssc.prototpe.parsers.HttpRequestLexParser;

public class HttpTestServer {

	private ServerSocket serverSocket;

	public HttpTestServer(int port) {
		try {
			serverSocket = new ServerSocket(port);
			
			System.out.println("Listening on " + InetAddress.getLocalHost() + ":" + port);
			
			while(true) {
				System.out.println("Starting to accept connection");
				Socket socket = serverSocket.accept();
				System.out.println("Accepted connection");
//				ByteBuffer buffer = ByteBuffer.allocate(1000);
//				socket.read(buffer);
//				print(buffer.array());
				
				char[] buf = new char[999];
				
//				Reader reader = new InputStreamReader(socket.getInputStream());
//				reader.read(buf);
//				System.out.print("\"");
//				System.out.print(buf);
//				System.out.println("\"");
//				String str = new String(buf);
				
				HttpRequestLexParser parser = new HttpRequestLexParser(socket.getInputStream());
				System.out.println("Created parser, parsing");
				parser.parse();
				System.out.println("Finished parsing");
				
				HttpRequest req = parser.getParsedRequest();
				
				System.out.println("Parsed request:");
				System.out.println("Method: " + req.getMethod());
				System.out.println("Path: " + req.getPath());
				System.out.println("Version: " + req.getVersion());
				
				Map<String, String> contentMap = req.getHeader().getContentMap();
				for(String key: contentMap.keySet()) {
					System.out.println("Field: \"" + key + "\"" + " - Value: \"" + contentMap.get(key) + "\"");
				}
				
				socket.close();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	private void print(byte[] buffer){
		byte last = 0;
		
		System.out.print("\"");
		for( int i = 0; i < buffer.length && buffer[i] != 0; i++){
			System.out.print((char)buffer[i]);
		}
		System.out.print("\"");
	}

	public static void main(String[] args) {
		System.out.println("Started HTTP test server...");
		new HttpTestServer(8080);
	}
}