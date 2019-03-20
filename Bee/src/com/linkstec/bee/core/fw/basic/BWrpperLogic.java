package com.linkstec.bee.core.fw.basic;

import java.util.List;

import com.linkstec.bee.core.fw.logic.BLogicUnit;

public interface BWrpperLogic extends BLogic {

	public List<BLogicUnit> getStartLogics();

	public List<BLogicUnit> getEndLogics();
}
