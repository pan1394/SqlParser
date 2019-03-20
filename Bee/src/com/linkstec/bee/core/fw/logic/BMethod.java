package com.linkstec.bee.core.fw.logic;

import java.util.List;

import com.linkstec.bee.core.fw.BAnnotable;
import com.linkstec.bee.core.fw.BParameter;
import com.linkstec.bee.core.fw.BType;
import com.linkstec.bee.core.fw.BValuable;
import com.linkstec.bee.core.fw.BVariable;

public interface BMethod extends BLogicUnit, BValuable, BAnnotable {

	public void setName(String name);

	public void addDefinedType(BType type);

	public List<BType> getDefinedTypes();

	public String getName();

	public String getLogicName();

	public void setLogicName(String name);

	public void setReturn(BValuable bclass);

	public void setDeclearedParentName(String name);

	public String getDeclearedParentName();

	public BValuable getReturn();

	public void addParameter(BParameter parameter);

	public List<BParameter> getParameter();

	public List<BVariable> getThrows();

	public void addThrows(BVariable exceptions);

	public void setLogicBody(BLogicBody body);

	public BLogicBody getLogicBody();

	public Object cloneAll();

	public void setModifier(int mods);

	public int getModifier();

}
