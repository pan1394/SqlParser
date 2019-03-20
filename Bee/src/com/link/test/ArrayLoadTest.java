package com.link.test;

public class ArrayLoadTest {
	public static void main(String[] args) {
		String s = "[Ljava.lang.String;";
		ClassLoader cl = ArrayLoadTest.class.getClassLoader();
		ClassLoader scl = cl.getSystemClassLoader();

		try {
			cl.loadClass(s);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			scl.loadClass(s);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			Class.forName(s);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
