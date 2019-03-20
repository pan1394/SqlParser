package com.link.test;

import java.net.MalformedURLException;
import java.net.URL;

public class ClassLoaderTest {

	public static void main(String[] args) {
		ClassLoader cl = Thread.currentThread().getContextClassLoader();
		URL url = cl.getResource("java/lang/String.class");
		System.out.println(url.toString());
		System.out.println(url.getFile());
		System.out.println(url.getPath());

		System.out.println(url.getRef());

		url = cl.getResource("com/link/test/ClassLoaderTest.class");
		System.out.println(url.toString());
		System.out.println(url.getFile());
		System.out.println(url.getPath());

		try {
			url = new URL("jar:file:/C:/Program%20Files/Java/jre1.8.0_181/lib/rt.jar!/java/lang/String.class");
			new URL("file:/D:/Bee/workspace/Bee/bin/com/link/test/ClassLoaderTest.class");

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
