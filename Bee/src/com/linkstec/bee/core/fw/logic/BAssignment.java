package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BAnnotable;
import com.linkstec.bee.core.fw.BParameter;

public interface BAssignment extends BAssign, BAnnotable {

	public void setLeft(BParameter parameter);

	public BParameter getLeft();

	// for data

	public void setMehodResotred(boolean restored);

	public boolean isMethodRestored();

}
