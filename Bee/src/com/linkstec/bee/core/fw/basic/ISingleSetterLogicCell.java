package com.linkstec.bee.core.fw.basic;

import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.logic.BInvoker;

public interface ISingleSetterLogicCell extends ILogicCell {

	public BInvoker getSetterParent();

	public BValuable getSetterParameter();
}
