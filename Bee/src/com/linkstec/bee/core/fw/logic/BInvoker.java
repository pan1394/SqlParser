package com.linkstec.bee.core.fw.logic;

import java.util.List;

import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BValuable;

public interface BInvoker extends BObject, BLogicUnit, BValuable {

	String TYPE_METHOD = "TYPE_METHOD";
	String TYPE_INNERCLASS = "TYPE_INNERCLASS";
	String TYPE_FIELD = "TYPE_FIELD";

	public BValuable getInvokeParent();

	public void setInvokeParent(BValuable parent);

	public BValuable getInvokeChild();

	public void setInvokeChild(BValuable child);

	public List<BValuable> getParameters();

	public void addParameter(BValuable parameter);

	public boolean isData();

	public void setData(boolean b, boolean setter);

	public boolean isStatic();

	public void setStatic(Boolean isStatic);

	// inner class calls method which is located on parent class
	public void setInnerClassCall(boolean innerCall);

	public boolean isInnerClassCall();

	public Object cloneAll();

	public boolean isLinker();

	public void setDataMethodName(String method);

	public String getDataMethodName();

	public void clearParameters();
}
