package com.linkstec.bee.core.fw.action;

import java.lang.reflect.Type;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;

public class BJavaGen {

	public static BJavaGenIF gen;

	public static String getTypeSource(BProject project, BClass bclass, BValuable value) {
		return gen.getBTypeSource(project, bclass, value);
	}

	public static String getClassHeaderSource(BProject project, BClass bclass) {
		return gen.getBClassHeaderSource(project, bclass);
	}

	public static String getValuableSource(BProject project, BClass bclass, BValuable value) {
		return gen.getBValuableSource(project, bclass, value);
	}

	public static String getUnitSource(BProject project, BClass bclass, BLogicUnit value) {
		return gen.getBUnitSource(project, bclass, value);
	}

	public static String getMethodSource(BProject project, BClass bclass, BMethod value) {
		return gen.getBMethodSource(project, bclass, value);
	}

	public static String getAllSource(BProject project, BClass bclass) {

		return gen.getBAllSource(project, bclass);
	}

	public static Class<?> getClassByName(BProject project, String name) {
		return gen.getClassByName(project, name);
	}

	public static BVariable makeValuableByType(Class<?> cls, Type type, BProject project) {
		return gen.makeValuableByType(cls, type, project);
	}

	public static BDocIF getDoc(BProject project, String name) {
		return gen.getDoc(project, name);
	}

	public static List<String> getAnnotation(BProject project, BClass bclass) {
		return gen.getAnnotationSource(project, bclass);
	}

}
