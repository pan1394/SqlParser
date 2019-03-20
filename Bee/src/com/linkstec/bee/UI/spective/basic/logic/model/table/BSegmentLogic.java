package com.linkstec.bee.UI.spective.basic.logic.model.table;

import java.util.List;

import com.linkstec.bee.core.fw.basic.BPath;
import com.linkstec.bee.core.fw.basic.ITableSegmentCell;
import com.linkstec.bee.core.fw.logic.BInvoker;
import com.linkstec.bee.core.impl.basic.BasicLogic;

public class BSegmentLogic extends BasicLogic {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6841671020342398716L;

	private String name;

	public BSegmentLogic(BPath parent, ITableSegmentCell cell) {
		super(parent, cell);
	}

	public List<BInvoker> getParameters() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
