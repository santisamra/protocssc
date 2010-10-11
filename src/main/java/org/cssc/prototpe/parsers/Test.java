package org.cssc.prototpe.parsers;

import java.io.FileReader;
import java.io.IOException;

public class Test {

	public static void main(String[] args) {
		HttpRequestParser parser = null;
		try {
			parser = new HttpRequestParser(new FileReader("src/main/resources/samples/request00.txt"));
			parser.parse();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
