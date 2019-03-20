package com.linkstec.bee.core.fw.logic;

import java.util.List;

public interface BMultiCondition extends BLogicUnit {

	public List<BConditionUnit> getConditionUnits();

	public void clearAllConditionUnit();

	public void addCondition(BConditionUnit unit);

}
