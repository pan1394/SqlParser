package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BObject;
import com.linkstec.bee.core.fw.BParameter;

public interface BCatchUnit extends BObject {

	public void setVariable(BParameter var);

	public BParameter getVariable();

	public BLogicBody getEditor();

	public void setEditor(BLogicBody editor);
}
