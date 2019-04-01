package com.linkstec.sql.constants;

public enum JoinType {

	LEFT,
	RIGHT,
	INNER;
	
	public static String[] getAll() {
		String[] res = {LEFT.toString(), RIGHT.toString(), INNER.toString()};
		return res;
	}
}
