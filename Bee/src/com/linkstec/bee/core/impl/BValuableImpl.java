package com.linkstec.bee.core.impl;

import com.linkstec.bee.core.fw.BValuable;

public abstract class BValuableImpl extends BObjectImpl implements BValuable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6448333943663049107L;

	private BValuable cast;
	private BValuable arrayIndex;
	private BValuable arrayObject;

	@Override
	public BValuable getCast() {
		return this.cast;
	}

	@Override
	public void setCast(BValuable cast) {
		this.cast = cast;
	}

	@Override
	public BValuable getArrayIndex() {
		return this.arrayIndex;
	}

	@Override
	public void setArrayIndex(BValuable index) {
		this.arrayIndex = index;
	}

	@Override
	public void setArrayObject(BValuable object) {
		this.arrayObject = object;
	}

	@Override
	public BValuable getArrayObject() {
		return this.arrayObject;
	}

}
