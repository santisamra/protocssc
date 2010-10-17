package org.cssc.prototpe.parsers;

import java.io.FileInputStream;
import java.io.IOException;

import org.cssc.prototpe.http.HttpResponse;

public class ResponseTest {

	public static void main(String[] args) {
		HttpResponseParser parser = null;
		try {
			parser = new HttpResponseParser(new FileInputStream("src/main/resources/samples/response00.res"));
			HttpResponse response = parser.parse();
			
			System.out.println("Content:");
			System.out.print(new String(response.getContent()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
