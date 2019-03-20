package com.linkstec.bee.core.impl;

import com.linkstec.bee.core.fw.BParameter;

public class BParameterImpl extends BVariableImpl implements BParameter {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5388020499668337068L;
	private int mod = 0;

	@Override
	public int getModifier() {
		return this.mod;
	}

	@Override
	public void setModifier(int modifier) {
		this.mod = modifier;
	}

}
