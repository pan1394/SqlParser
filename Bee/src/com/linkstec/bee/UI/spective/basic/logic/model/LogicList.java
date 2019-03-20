package com.linkstec.bee.UI.spective.basic.logic.model;

import java.io.Serializable;
import java.util.List;

import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.BPath;

public abstract class LogicList implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 830933257978548801L;

	public abstract List<BLogic> getList(BPath parent);
}