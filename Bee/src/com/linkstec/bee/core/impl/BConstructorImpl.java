package com.linkstec.bee.core.impl;

import java.io.Serializable;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.logic.BConstructor;

public class BConstructorImpl extends BMethodImpl implements BConstructor, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1288950882805063428L;

	private BClass body;

	@Override
	public void setBody(BClass bclass) {
		this.body = bclass;
	}

	@Override
	public BClass getBody() {
		return this.body;
	}

}
