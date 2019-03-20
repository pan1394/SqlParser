package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BModifiedBlock extends BLogicBody, BLogicUnit {

	public void setVariable(BValuable obj);

	public BValuable getVariable();

	public void setMods(int mod);

	public int getMods();
}
