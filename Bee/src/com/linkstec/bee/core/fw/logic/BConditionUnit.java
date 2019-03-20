package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BConditionUnit {

	public BValuable getCondition();

	public BLogicBody getLogicBody();

	public void setCondition(BValuable object);

	public void clearConditions();

	public void setLast(boolean last);

	public boolean isLast();
}
