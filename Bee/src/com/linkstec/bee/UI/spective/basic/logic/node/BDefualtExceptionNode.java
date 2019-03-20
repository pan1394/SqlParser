package com.linkstec.bee.UI.spective.basic.logic.node;

import com.linkstec.bee.core.fw.basic.BLogic;
import com.linkstec.bee.core.fw.basic.IExceptionCell;

public class BDefualtExceptionNode extends BNode implements IExceptionCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 395405816582735973L;

	public BDefualtExceptionNode() {
		this.setValue("エラーを処理しない？");
		this.setVertex(true);
		this.setStyle("strokeWidth=0.5;strokeColor=gray;fillColor=red;fontColor=white");
	}

	@Override
	public IExceptionCell getExcetion() {
		return null;
	}

	@Override
	public BLogic getLogic() {
		return null;
	}

}
