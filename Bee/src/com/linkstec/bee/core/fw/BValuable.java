package com.linkstec.bee.core.fw;

/**
 * 
 * Used by BVariable,BExpression,BExpressionLine,BMethod
 *
 */
public interface BValuable extends BObject {

	public BClass getBClass();

	public void setCast(BValuable cast);

	public BType getParameterizedTypeValue();

	public BValuable getCast();

	// for array
	public BValuable getArrayIndex();

	// for array
	public void setArrayIndex(BValuable index);

	// when array is accessed,set the array information here
	public void setArrayObject(BValuable object);

	// for array
	public BValuable getArrayObject();
}
