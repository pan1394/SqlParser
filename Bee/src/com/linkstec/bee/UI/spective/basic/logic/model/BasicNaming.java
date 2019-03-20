package com.linkstec.bee.UI.spective.basic.logic.model;

import java.io.Serializable;

import com.linkstec.bee.core.fw.BClass;

public class BasicNaming implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5506201109592907300L;

	public static String getFieldName(BClass cls, BClass type) {
		return "m" + type.getLogicName().toLowerCase();
	}

	public static String getVarName(BClass type) {
		return "m" + type.getLogicName().toLowerCase();
	}
}
