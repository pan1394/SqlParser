package com.linkstec.bee.core.fw;

import java.util.ArrayList;
import java.util.List;

public class BUtils {

	public static String getClassQulifiedName(BClass bclass, String logicName) {

		String name;
		if (bclass.isArray()) {
			BType array = bclass.getArrayPressentClass();
			if (array.isArray()) {
				if (array instanceof BClass) {
					BClass b = (BClass) array;
					name = "[" + b.getQualifiedName();
				} else {
					name = "[" + array.getLogicName();
				}
			} else {
				if (array instanceof BClass) {
					BClass b = (BClass) array;
					if (b.isPrimitive()) {
						return BUtils.getPrimeryArrayClassName(b.getQualifiedName());
					} else {
						name = "[L" + b.getQualifiedName() + ";";
					}
				} else {
					name = "[L" + array.getLogicName() + ";";
				}

			}
			return name;
		}

		if (bclass.getPackage() != null && bclass.getPackage() != null) {
			name = bclass.getPackage() + "." + logicName;
		} else {
			name = bclass.getLogicName();
		}
		return name;
	}

	public static List<String> getImport(BClass bclass, String simpleName) {
		List<BImport> imports = bclass.getImports();

		List<String> list = new ArrayList<String>();

		for (BImport imp : imports) {
			String logicName = imp.getLogicName();
			if (logicName.endsWith(".*")) {

			} else {
				String name = imp.getSimleName();
				int index = name.indexOf("$");
				if (index > 0) {
					name = name.substring(index + 1);
				}
				if (name.equals(simpleName)) {
					list.add(imp.getLogicName());
				}
			}

		}
		for (BImport imp : imports) {
			String logicName = imp.getLogicName();
			if (logicName.endsWith(".*")) {
				list.add(logicName.replace("*", simpleName));
			}
		}
		return list;
	}

	public static Class<?> getPrimeryClass(String name) {
		if (name == null) {
			return void.class;
		}
		switch (name) {
		case "double":
			return double.class;
		case "int":
			return int.class;
		case "float":
			return float.class;
		case "long":
			return long.class;
		case "short":
			return short.class;
		case "byte":
			return byte.class;
		case "void":
			return void.class;
		case "boolean":
			return boolean.class;
		case "char":
			return char.class;

		}

		return null;
	}

	public static boolean isPrimeryClass(String name) {
		if (name == null) {
			return false;
		}
		switch (name) {
		case "double":
			return true;
		case "int":
			return true;
		case "float":
			return true;
		case "long":
			return true;
		case "short":
			return true;
		case "byte":
			return true;
		case "void":
			return true;
		case "boolean":
			return true;
		case "char":
			return true;

		}

		return false;
	}

	public static Class<?> getPrimeryArrayClass(String name) {
		if (name.equals("[I")) {
			return int[].class;
		} else if (name.equals("[J")) {
			return long[].class;
		} else if (name.equals("[S")) {
			return short[].class;
		} else if (name.equals("[D")) {
			return double[].class;
		} else if (name.equals("[F")) {
			return float[].class;
		} else if (name.equals("[B")) {
			return byte[].class;
		} else if (name.equals("[Z")) {
			return boolean[].class;
		} else if (name.equals("[C")) {
			return char[].class;
		} else {
			return null;
		}
	}

	public static String getPrimeryArrayClassName(String name) {
		if (name == null) {
			return null;
		}
		switch (name) {
		case "double":
			return "[D";
		case "int":
			return "[I";
		case "float":
			return "[F";
		case "long":
			return "[J";
		case "short":
			return "[S";
		case "byte":
			return "[B";
		case "boolean":
			return "[Z";
		case "char":
			return "[C";
		}

		return null;
	}

}
