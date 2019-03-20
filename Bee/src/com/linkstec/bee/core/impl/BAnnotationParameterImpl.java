package com.linkstec.bee.core.impl;

import com.linkstec.bee.core.fw.BAnnotationParameter;
import com.linkstec.bee.core.fw.BValuable;

public class BAnnotationParameterImpl extends BVariableImpl implements BAnnotationParameter {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4311246608527514890L;
	private BValuable value;

	@Override
	public void setValue(BValuable value) {
		this.value = value;
	}

	@Override
	public BValuable getValue() {
		return this.value;
	}

}
