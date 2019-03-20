package com.linkstec.bee.core.fw.logic;

import java.util.List;

import com.linkstec.bee.core.fw.BObject;

public interface BLogicBody extends BObject {

	public List<BLogicUnit> getUnits();

	public void setUnits(List<BLogicUnit> units);

	public void addUnit(BLogicUnit unit);

	public void addUnit(BLogicUnit unit, int index);

	public void clear();
}
