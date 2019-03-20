package com.link.test;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ParameterTest<A, B> {
	A a;

	public ParameterTest(A a) {
		this.a = a;
		this.getList(new Hashtable(), new ArrayList<String>(), a);
		this.getObject("a");
	}

	public List<String> getList(Hashtable<String, Object> hash, List<?> list, A a) {
		return null;
	}

	public void getObject(Object o) {

	}

	public void make(String s, String... objects) {

	}

	public static void main(String[] args) {
		Class<?> cls = String.class;
		try {
			Method m = cls.getMethod("format", String.class, Object[].class);
			// Method[] ms = cls.getMethods();
			// for (Method m : ms) {
			// if (m.getName().equals("format")) {
			Parameter[] paras = m.getParameters();
			for (Parameter para : paras) {
				System.out.println(para.isImplicit());
				System.out.println(para.isNamePresent());
				System.out.println(para.isVarArgs());
				System.out.println(para.toString());
				System.out.println(para.getAnnotatedType().getType().getTypeName());
				System.out.println(para.getType().getName());
			}
			// }

			// }
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}
