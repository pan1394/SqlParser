package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BValuable;

public interface BExpressionLine extends BObject, BValuable {

	public BValuable getCondition();

	public void setCondition(BValuable object);

	public void setTrue(BValuable object);

	public BValuable getTrue();

	public void setFalse(BValuable object);

	public BValuable getFalse();

}
