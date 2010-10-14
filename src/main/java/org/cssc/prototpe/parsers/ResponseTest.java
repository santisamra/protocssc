package org.cssc.prototpe.parsers;

import java.io.FileReader;
import java.io.IOException;

import org.cssc.prototpe.http.HttpResponse;

public class ResponseTest {

	public static void main(String[] args) {
		HttpResponseParser parser = null;
		try {
			parser = new HttpResponseParser(new FileReader("src/main/resources/samples/response00.txt"));
			parser.parse();
			
			HttpResponse response = parser.getParsedResponse();
			
			for(byte b: response.getContent()) {
				System.out.print(b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
