package com.linkstec.bee.core.impl;

import java.io.Serializable;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.List;

import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.editor.BProject;

public class BSudoClass extends BClassImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4459555657157064960L;

	private String name;
	private String project;

	public void setClass(Class<?> cls, BProject project) {
		this.name = cls.getName();
		this.setLogicName(name.substring(name.lastIndexOf(".") + 1));
		if (project != null) {
			this.project = project.getName();
		}

		String name = cls.getName();
		String p = null;
		if (name.indexOf('.') > 0) {
			name = name.substring(0, name.lastIndexOf('.'));
			p = name;
		}
		this.setPackage(p);
		this.setInterface(cls.isInterface());
		this.setException(CodecUtils.isException(cls));
		this.setModifier(cls.getModifiers());
		if (cls.isEnum()) {
			this.setEnum();
		}

		boolean isdata = CodecUtils.isData(cls);
		if (isdata) {
			this.setData(true);
		} else {
			this.setLogic(true);
		}

	}

	@Override
	public List<BType> getParameterizedTypes() {
		List<BType> ts = new ArrayList<BType>();
		BProject p = Application.getInstance().getConfigSpective().getConfig().getProject(project);
		Class<?> cls = CodecUtils.getClassByName(name, p);
		TypeVariable<?>[] vars = cls.getTypeParameters();
		for (TypeVariable<?> var : vars) {
			ts.add(CodecUtils.makeValuableByType(var, p));
		}
		ts.addAll(types);
		return ts;
	}

	@Override
	public BValuable getSuperClass() {

		BProject p = Application.getInstance().getConfigSpective().getConfig().getProject(project);
		Class<?> cls = CodecUtils.getClassByName(name, p);
		Class<?> superClass = cls.getSuperclass();
		if (superClass != null) {
			return CodecUtils.makeClassValuableByType(superClass, cls.getGenericSuperclass(), p);
		}
		return null;

	}

	@Override
	public List<BValuable> getInterfaces() {
		BProject p = Application.getInstance().getConfigSpective().getConfig().getProject(project);
		Class<?> cls = CodecUtils.getClassByName(name, p);
		Class<?>[] inters = cls.getInterfaces();

		List<BValuable> values = new ArrayList<BValuable>();
		for (Class<?> in : inters) {
			values.add(CodecUtils.makeClassValuableByType(in, in.getGenericSuperclass(), p));
		}
		return values;
	}

}
