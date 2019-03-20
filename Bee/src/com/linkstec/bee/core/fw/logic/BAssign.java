package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BAssign extends BLogicUnit {

	public BLogiker getAssignment();

	public void setRight(BValuable right, BLogiker assign);

	public BValuable getRight();
}
