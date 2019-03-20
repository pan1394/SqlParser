package com.linkstec.bee.UI.spective.basic.logic.node;

import java.util.List;

import com.linkstec.bee.UI.spective.basic.logic.model.common.BasicStartLogic;
import com.linkstec.bee.core.codec.basic.BasicGenUtils;
import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;
import com.linkstec.bee.core.fw.basic.ILogicCell;
import com.linkstec.bee.core.fw.basic.IUnitCell;

public class BFlowStart extends BNode implements IUnitCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4193039491751890540L;
	private BasicStartLogic logic;

	public BFlowStart(BasicStartLogic logic) {
		this.logic = logic;
		this.setVertex(true);
		this.getGeometry().setWidth(50);
		this.getGeometry().setHeight(50);
		this.setValue("S");
		this.setStyle("ellipse;shape=doubleEllipse;strokeWidth=0.5;strokeColor=gray;align=center");
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return logic;
	}

	public List<ILogicCell> getNexts() {
		return BasicGenUtils.getNexts(this, this.getLogic().getPath());
	}

}
