package com.linkstec.bee.core.fw.logic;

import java.util.List;

import com.linkstec.bee.core.fw.BValuable;

public interface BSwitchUnit extends BLogicUnit {

	public void setVariable(BValuable variable);

	public BValuable getVariable();

	public List<BConditionUnit> getConditionUnits();

	public void addCondition(BConditionUnit condition);

	public BConditionUnit makeDefault();
}
