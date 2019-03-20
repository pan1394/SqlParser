package com.link.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Test<T extends List<String>, S> extends Hashtable<String[], String> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 6570331641724183357L;
	private String t;

	// <C> C b=null;

	public Test(String a) {
		this.t = a;

	}

	public <B> B getSometho(Object a) {

		B abc = null;
		return null;
	}

	public void setValue(S d) {

	}

	public ArrayList<String> list = new ArrayList<String>();

	public static void main(String[] args) {
		Test t = new Test("bb");
		t.list.add("aa");
		System.out.println(t.list);

		Test t1 = (Test) t.clone();

		t1.list.add("bb");
		System.out.println(t1.list);

		System.out.println(t.list);
	}

}
