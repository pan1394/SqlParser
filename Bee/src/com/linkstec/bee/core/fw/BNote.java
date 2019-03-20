package com.linkstec.bee.core.fw;

import com.linkstec.bee.core.fw.logic.BLogicUnit;

public interface BNote extends BObject, BLogicUnit {

	public String getNote();

	public void setNote(String note);
}
