package com.linkstec.bee.core.fw.basic;

import java.util.List;

import com.linkstec.bee.core.fw.logic.BInvoker;

public interface BJudgeLogic extends BLogic {

	public BLogic getLogic();

	public List<BLogic> getYes();

	public List<BLogic> getNo();

	public String getSql();

	public String getLogicSql(ITableSql tsql);

	public List<BInvoker> getInvokers();
}
