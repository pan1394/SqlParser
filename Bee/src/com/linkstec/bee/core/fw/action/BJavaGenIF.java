package com.linkstec.bee.core.fw.action;

import java.lang.reflect.Type;
import java.util.List;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;
import com.linkstec.bee.core.fw.editor.BProject;
import com.linkstec.bee.core.fw.logic.BLogicUnit;
import com.linkstec.bee.core.fw.logic.BMethod;

public interface BJavaGenIF {

	public List<String> getAnnotationSource(BProject project, BClass bclass);

	public String getBTypeSource(BProject project, BClass bclass, BValuable value);

	public String getBClassHeaderSource(BProject project, BClass bclass);

	public String getBValuableSource(BProject project, BClass bclass, BValuable value);

	public String getBUnitSource(BProject project, BClass bclass, BLogicUnit value);

	public String getBMethodSource(BProject project, BClass bclass, BMethod value);

	public String getBAllSource(BProject project, BClass bclass);

	public Class<?> getClassByName(BProject project, String name);

	public BVariable makeValuableByType(Class<?> cls, Type type, BProject project);

	public BDocIF getDoc(BProject project, String name);
}
