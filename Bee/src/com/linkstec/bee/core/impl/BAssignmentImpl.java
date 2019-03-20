package com.linkstec.bee.core.impl;

import java.io.Serializable;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.ILogic;
import com.linkstec.bee.core.fw.NodeNumber;
import com.linkstec.bee.core.fw.logic.BAssignment;

public class BAssignmentImpl extends BAssignImpl implements BAssignment, Serializable, ILogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6130123003326391141L;

	private BParameter parameter;
	private boolean methodRestord = false;

	@Override
	public void setLeft(BParameter parameter) {
		this.parameter = parameter;
	}

	@Override
	public BParameter getLeft() {
		return this.parameter;
	}

	@Override
	public String getName() {
		return this.parameter.getName();
	}

	@Override
	public String getLogicName() {
		return this.parameter.getLogicName();
	}

	@Override
	public NodeNumber getNumber() {
		return null;
	}

	@Override
	public void setMehodResotred(boolean restored) {
		this.methodRestord = restored;
	}

	@Override
	public boolean isMethodRestored() {
		return methodRestord;
	}
}
