package com.linkstec.bee.core.fw;

import java.util.List;

import com.linkstec.bee.core.fw.logic.BInvoker;

/*
 * it will be a view which can be selected and edit by human operation
 */
public interface IUnit {

	public NodeNumber getNumber();

	public void setNumber(NodeNumber number);

	public List<BInvoker> getLinkers();

	public void makeDefualtValue(Object target);

}
