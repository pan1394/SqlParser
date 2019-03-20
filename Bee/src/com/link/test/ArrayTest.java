package com.link.test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ArrayTest implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8653773702370677617L;
	private List<CharTest> chars = new ArrayList<CharTest>();

	public ArrayTest() {
		System.out.println(System.currentTimeMillis() + this.hashCode());
		chars.add(new CharTest());
	}

	public CharTest getChar() {
		return chars.get(0);
	}

	public static void main(String[] args) {

		String[] lib = new String[] { "a", "b", "u", "d", "e", "n", "m", "h", "o", "k" };

		String s = "日本語の場合にこれを設定する、それでない場合に";
		int leng = s.length();
		int c = s.hashCode();

		String sr = String.valueOf(c);

		leng = sr.length();
		String re = "";
		for (int i = 0; i < leng; i++) {
			String t = sr.substring(i, i + 1);
			int in = Integer.valueOf(t);
			re = re + lib[in];
		}

		System.out.println(re);
	}

}
