package org.cssc.prototpe.parsers;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.cssc.prototpe.http.HttpRequest;

public class RequestTest {

	public static void main(String[] args) {
		HttpRequestLexParser parser = null;
		try {
			parser = new HttpRequestLexParser(new FileReader("src/main/resources/samples/request00.txt"));
			parser.parse(); 
			
			HttpRequest req = parser.getParsedRequest();
			
			System.out.println("Parsed request:");
			System.out.println("Method: " + req.getMethod());
			System.out.println("Path: " + req.getPath());
			System.out.println("Version: " + req.getVersion());
			
			Map<String, String> contentMap = req.getHeader().getContentMap();
			for(String key: contentMap.keySet()) {
				System.out.println("Field: \"" + key + "\"" + " - Value: \"" + contentMap.get(key) + "\"");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
