package com.linkstec.bee.core.fw.logic;

import java.util.List;

import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BValuable;

public interface BLambda extends BValuable {

	public void addParameter(BParameter parameter);

	public List<BParameter> getParameter();

	public void setLogicBody(BLogicBody body);

	public BLogicBody getLogicBody();
}
