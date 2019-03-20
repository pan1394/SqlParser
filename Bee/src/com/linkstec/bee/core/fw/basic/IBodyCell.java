package com.linkstec.bee.core.fw.basic;

import java.util.List;

public interface IBodyCell extends IUnitCell {

	public List<ILogicCell> getLogics();

	public ILogicCell getStart();

}
