package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BReturnUnit extends BLogicUnit {

	public static final int RETURN_VOID = 1;
	public static final int RETURN_BOOLEAN = 2;
	public static final int RETURN_OTHER = 3;

	public BValuable getReturnValue();

	public void setRuturnValue(BValuable obj);

	public void setRuturnNullValue();

}
