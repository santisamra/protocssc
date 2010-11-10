package org.cssc.prototpe.parsers;

import java.io.FileInputStream;
import java.io.IOException;

public class ResponseTester {

	public static void main(String[] args) {
		HttpResponseParser parser = null;
		try {
			parser = new HttpResponseParser(new FileInputStream("src/main/resources/samples/response00.res"));
			parser.parse();
			
			byte[] temp;
			
			while((temp = parser.readNextChunk()) != null) {
				System.out.println("\"" + new String(temp) + "\"");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
