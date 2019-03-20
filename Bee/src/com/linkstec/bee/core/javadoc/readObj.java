package com.linkstec.bee.core.javadoc;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;

public class readObj {

	private static BufferedInputStream bis;

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		File input = new File("D:\\test\\java\\io\\File.obj");
		InputStream is = new FileInputStream(input);
		bis = new BufferedInputStream(is);
		byte[] buff = new byte[1000000000];
		DocClass d;
		bis.read(buff, 0, buff.length);
		ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(buff));
		d = (DocClass) in.readObject();
		System.out.println("ok");
	}

}
