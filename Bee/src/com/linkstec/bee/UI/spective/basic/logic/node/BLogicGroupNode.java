package com.linkstec.bee.UI.spective.basic.logic.node;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.model.BGroupLogic;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IBodyCell;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;

public class BLogicGroupNode extends BGroupNode implements IBodyCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4937487201796289491L;

	private String logicName;
	private BGroupLogic logic;

	public BLogicGroupNode(BGroupLogic logic) {
		this.logic = logic;
	}

	public String getLogicName() {
		return logicName;
	}

	public void setLogicName(String logicName) {
		this.logicName = logicName;
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return this.logic;
	}

	@Override
	public List<ILogicCell> getLogics() {
		return null;
	}

	@Override
	public ILogicCell getStart() {
		return BasicGenUtils.getStart(this);
	}

}