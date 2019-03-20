package com.linkstec.bee.core.fw.logic;

import java.io.Serializable;
import java.lang.reflect.Modifier;

public class BMod implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2055631995298125274L;

	public static final String S_PUBLIC = "public";
	public static final String S_PRIVATE = "private";
	public static final String S_PROTECTED = "protected";
	public static final String S_STATIC = "static";
	public static final String S_FINAL = "final";
	public static final String S_ABSTRACT = "abstract";
	public static final String S_NATIVE = "native";
	public static final String S_SYNCHRONIZED = "synchronized";
	public static final String S_TRANSIENT = "transient";
	public static final String S_VOLATILE = "volatile";

	public static int getType(String s) {
		switch (s) {
		case S_PUBLIC:
			return Modifier.PUBLIC;
		case S_PRIVATE:
			return Modifier.PRIVATE;
		case S_PROTECTED:
			return Modifier.PROTECTED;
		case S_STATIC:
			return Modifier.STATIC;
		case S_FINAL:
			return Modifier.FINAL;
		case S_ABSTRACT:
			return Modifier.ABSTRACT;
		case S_NATIVE:
			return Modifier.NATIVE;
		case S_SYNCHRONIZED:
			return Modifier.SYNCHRONIZED;
		case S_TRANSIENT:
			return Modifier.TRANSIENT;
		case S_VOLATILE:
			return Modifier.VOLATILE;
		default:
			return 0;
		}
	}
}
