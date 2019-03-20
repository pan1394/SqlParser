package com.linkstec.bee.core.impl;

import java.io.Serializable;

import com.linkstec.bee.core.Application;
import com.linkstec.bee.core.codec.util.CodecUtils;
import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.editor.BProject;

public class BClassVariable extends BValuableImpl implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8123493171323288644L;
	private String className;
	private String project;
	private BType type;

	@Override
	public BClass getBClass() {
		BProject p = Application.getInstance().getConfigSpective().getConfig().getProject(project);
		return CodecUtils.getClassFromJavaClass(p, className);
	}

	@Override
	public BType getParameterizedTypeValue() {
		return type;
	}

	public void setBClass(String clsName, BProject project) {
		this.className = clsName;
		if (project != null) {
			this.project = project.getName();
		}
	}

	public void setParameterizedTypeValue(BType type) {
		this.type = type;
	}

}
