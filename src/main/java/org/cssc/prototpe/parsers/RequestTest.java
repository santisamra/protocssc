package org.cssc.prototpe.parsers;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;

import org.cssc.prototpe.http.HttpRequest;

public class RequestTest {

	public static void main(String[] args) {
		HttpRequestParser parser = null;
		try {
			parser = new HttpRequestParser(new FileInputStream("src/main/resources/samples/request00.req"));
			HttpRequest req = parser.parse();
			
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
