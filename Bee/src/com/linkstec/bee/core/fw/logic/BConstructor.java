package com.linkstec.bee.core.fw.logic;

import com.linkstec.bee.core.fw.BClass;

public interface BConstructor extends BMethod {

	public void setBody(BClass bclass);

	public BClass getBody();

}
