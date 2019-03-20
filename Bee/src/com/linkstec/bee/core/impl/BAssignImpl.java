package com.linkstec.bee.core.impl;

import java.io.Serializable;

import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.logic.BAssign;
import com.linkstec.bee.core.fw.logic.BLogiker;

public abstract class BAssignImpl extends BObjectImpl implements BAssign, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6130123003326391141L;

	private BValuable right;
	private String label;
	private BLogiker logiker = null;

	@Override
	public void setRight(BValuable right, BLogiker assign) {
		this.logiker = assign;
		this.right = right;
	}

	@Override
	public BValuable getRight() {
		return this.right;
	}

	@Override
	public void setLabel(String label) {
		this.label = label;

	}

	@Override
	public String getLabel() {
		return this.label;
	}

	@Override
	public BLogiker getAssignment() {
		return this.logiker;
	}

	public abstract BValuable getLeft();

}
