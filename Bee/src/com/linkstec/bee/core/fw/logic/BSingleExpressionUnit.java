package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BValuable;

public interface BSingleExpressionUnit extends BLogicUnit, BValuable {

	public static final String INCREMENT = "++";
	public static final String DECREMENT = "--";

	public static final String UNARY_MINUS = "-";
	public static final String UNARY_PLUS = "-";

	public static final String INCREMENT_BEFORE = "B++";
	public static final String DECREMENT_BEFORE = "B--";

	public static final String COMPLEMENT = "~";

	public BValuable getVariable();

	public void setVariable(BValuable variable);

	public void setOperator(String operator);

	public String getOperator();
}
