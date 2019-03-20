package com.linkstec.bee.core.fw;

import java.util.List;

public interface BVariable extends BValuable {

	public void setVarArgs(boolean args);

	public boolean isVarArgs();

	public void setParameterizedTypeValue(BType type);

	public void setBClass(BClass type);

	public void setName(String name);

	public String getName();

	public String getLogicName();

	public void setLogicName(String name);

	public void addUnionType(BVariable var);

	public List<BVariable> getUnionTypes();

	public void setAnnotation(boolean anno);

	public boolean isAnnotation();

	// public void addAnnotationParameter(BObject parameter);

	// public List<BObject> getAnnotationParameters();

	public void setWildCard();

	public boolean isWildCard();

	public void setCaller(boolean caller);

	public boolean isCaller();

	public void makeCaller(BClass bclass, boolean self);

	public boolean isClass();

	public void setClass(boolean isClass);

	public void setNewClass(boolean newClass);

	public boolean isNewClass();

	// for array
	public void addArrayDimension(BValuable d);

	// for array
	public List<BValuable> getArrayDimensions();

	// for array
	public void addInitValue(BValuable value);

	public void clearInitValues();

	// for array
	public List<BValuable> getInitValues();

	public void replaceInitValue(int index, BValuable value);

	// for array
	public void setArrayTitle(boolean titled);

	// for array
	public boolean isArrayTitled();

}
