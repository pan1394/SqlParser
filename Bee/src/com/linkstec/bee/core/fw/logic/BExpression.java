package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BValuable;

public interface BExpression extends BObject, BValuable {

	public void setExLeft(BValuable left);

	public BValuable getExLeft();

	public void setExRight(BValuable right);

	public BValuable getExRight();

	public void setExMiddle(BLogiker logiker);

	public BLogiker getExMiddle();

	public void setParenthesized(boolean flg);

	public boolean isParenthesized();

}
