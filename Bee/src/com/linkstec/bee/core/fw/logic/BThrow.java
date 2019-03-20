package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BThrow extends BLogicUnit {

	public void setExcetion(BValuable exception);

	public BValuable getException();
}
