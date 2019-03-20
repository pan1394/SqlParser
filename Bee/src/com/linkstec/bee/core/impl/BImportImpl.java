package com.linkstec.bee.core.impl;

import java.io.Serializable;

import com.linkstec.bee.core.fw.BImport;

public class BImportImpl implements BImport, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -219523757725290036L;

	private String logicName;

	@Override
	public void setLogicName(String name) {
		this.logicName = name;
	}

	@Override
	public String getLogicName() {
		return logicName;
	}

	@Override
	public String getSimleName() {
		if (logicName != null) {
			if (logicName.indexOf('.') > 0) {
				return logicName.substring(logicName.lastIndexOf('.') + 1);
			} else {
				return logicName;
			}
		}
		return null;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
