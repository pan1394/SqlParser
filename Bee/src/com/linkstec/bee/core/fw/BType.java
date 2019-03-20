package com.linkstec.bee.core.fw;

import java.util.List;

public interface BType extends BObject {

	public void setName(String name);

	// only for parameterized type
	public void setOwnerType(BType type);

	public BType getOwnerType();

	public String getName();

	public String getLogicName();

	public void setLogicName(String name);

	public void addParameterizedType(BType type);

	public List<BType> getParameterizedTypes();

	public void setParameterTypes(List<BType> types);

	public boolean isParameterized();

	public boolean isTypeVariable();

	public boolean isClass();

	public void addBound(String type);

	public boolean isRawType();

	public void setRowType(boolean raw);

	public List<String> getBounds();

	public BType getArrayPressentClass();

	public void setArrayPressentClass(BType bclass);

	public boolean isArray();

	public boolean isWild();

	public void clearParameterTypes();

	public boolean isContainer();

	public boolean isParameterValue();

	public void setParameterValue(boolean value);

}
