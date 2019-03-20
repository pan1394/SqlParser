package com.linkstec.bee.core.javadoc;

import java.io.IOException;

public class DocReader {
	public static void main(String[] args) {
		java.io.InputStream s = DocReader.class.getResourceAsStream("/java/lang/String.d");
		byte[] buffer = new byte[1024];
		try {
			s.read(buffer);
			s.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println(new String(buffer));
	}
}
