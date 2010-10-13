package org.cssc.prototpe.parsers;

import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import org.cssc.prototpe.http.HttpRequest;

public class ResponseTest {

	public static void main(String[] args) {
		HttpResponseParser parser = null;
		try {
			parser = new HttpResponseParser(new FileReader("src/main/resources/samples/response00.txt"));
			parser.parse(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
