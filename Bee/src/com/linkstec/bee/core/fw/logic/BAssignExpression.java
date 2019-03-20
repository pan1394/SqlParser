package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BAssignExpression extends BAssign, BValuable {

	public void setLeft(BValuable left);

	public BValuable getLeft();
}
