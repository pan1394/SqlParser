package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BAssert extends BLogicUnit {

	public void setExpression(BValuable value);

	public BValuable getExpression();
}
