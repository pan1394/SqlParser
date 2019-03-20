package com.linkstec.bee.core.impl;

import com.linkstec.bee.core.fw.BClass;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.logic.BAssignExpression;

public class BAssignExpressionImpl extends BAssignImpl implements BAssignExpression {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7289853479776127114L;

	private BValuable left;

	@Override
	public void setLeft(BValuable left) {
		this.left = left;
	}

	@Override
	public BValuable getLeft() {
		return this.left;
	}

	@Override
	public BClass getBClass() {
		return this.getLeft().getBClass();
	}

	@Override
	public void setCast(BValuable cast) {
		this.getLeft().setCast(cast);
	}

	@Override
	public BValuable getCast() {
		return this.getLeft().getCast();
	}

	@Override
	public BType getParameterizedTypeValue() {
		return this.getLeft().getParameterizedTypeValue();
	}

	@Override
	public BValuable getArrayIndex() {
		return this.getLeft().getArrayIndex();
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.getLeft().setArrayIndex(index);
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.getLeft().setArrayObject(object);
	}

	@Override
	public BValuable getArrayObject() {
		return this.getLeft().getArrayObject();
	}

}
