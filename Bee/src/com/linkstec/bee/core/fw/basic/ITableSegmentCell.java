package com.linkstec.bee.core.fw.basic;

import java.util.List;

import com.linkstec.bee.core.fw.logic.BInvoker;

public interface ITableSegmentCell extends ILogicCell {

	public List<BInvoker> getParameters();
}
